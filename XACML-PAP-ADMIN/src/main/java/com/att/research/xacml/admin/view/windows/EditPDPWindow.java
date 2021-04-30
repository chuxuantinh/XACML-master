/*
 *                        AT&T - PROPRIETARY
 *          THIS FILE CONTAINS PROPRIETARY INFORMATION OF
 *        AT&T AND IS NOT TO BE DISCLOSED OR USED EXCEPT IN
 *             ACCORDANCE WITH APPLICABLE AGREEMENTS.
 *
 *          Copyright (c) 2014 AT&T Knowledge Ventures
 *              Unpublished and Not for Publication
 *                     All Rights Reserved
 */
package com.att.research.xacml.admin.view.windows;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.att.research.xacml.admin.util.AdminNotification;
import com.att.research.xacml.api.pap.PDP;
import com.att.research.xacml.api.pap.PDPGroup;
import com.vaadin.annotations.AutoGenerated;
import com.vaadin.data.Buffered.SourceException;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import elemental.events.KeyboardEvent.KeyCode;

public class EditPDPWindow extends Window {

	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	@AutoGenerated
	private VerticalLayout mainLayout;
	@AutoGenerated
	private Button buttonSave;
	@AutoGenerated
	private TextArea textDescription;
	@AutoGenerated
	private TextField textName;
	@AutoGenerated
	private TextField textId;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final EditPDPWindow self = this;
	private final PDP pdp;
	private final List<PDPGroup> groups;
	private boolean isSaved = false;
	/**
	 * The constructor should first build the main layout, set the
	 * composition root and then do any custom initialization.
	 *
	 * The constructor will not be automatically regenerated by the
	 * visual editor.
	 * @param pdp 
	 */
	public EditPDPWindow(PDP pdp, List<PDPGroup> list) {
		buildMainLayout();
		//setCompositionRoot(mainLayout);
		setContent(mainLayout);
		//
		// Save data
		//
		this.pdp = pdp;
		this.groups = list;
		//
		// Initialize
		//
		this.initializeText();
		this.initializeButton();
		//
		// Keyboard short
		//
		this.setCloseShortcut(KeyCode.ESC);
		this.buttonSave.setClickShortcut(KeyCode.ENTER);
		//
		//  Focus
		//
		this.textId.focus();
	}
	
	protected void initializeText() {
		//
		// Initialize values
		//
		if (this.pdp != null) {
			this.textId.setValue(this.pdp.getId());
			this.textName.setValue(this.pdp.getName());
			this.textDescription.setValue(this.pdp.getDescription());
		}
		//
		//
		//
		this.textId.setRequiredError("You must enter a valid id for the PDP.");
		this.textId.setNullRepresentation("");
		this.textId.addValidator(new RegexpValidator("[\\w=,]", false, "Please enter a valid URL with no whitespace or \"=\" or \",\" characters."));
		this.textId.addValidator(new Validator() {
			private static final long serialVersionUID = 1L;

			@Override
			public void validate(Object value) throws InvalidValueException {
				//
				// Cannot be null
				//
				if (value == null || value.toString().length() == 0) {
					throw new InvalidValueException("ID cannot be null.");
				}
				//
				// Make sure its a valid URL
				//
				try {
					new URL(value.toString());
				} catch (MalformedURLException e) {
					throw new InvalidValueException("The PDP URL '" + value.toString() + "' is not a valid URL: '" + e.getMessage() +"'");
				}
			}
		});
		//
		//
		//
		this.textName.setNullRepresentation("");
		this.textName.addValidator(new Validator() {
			private static final long serialVersionUID = 1L;

			@Override
			public void validate(Object value) throws InvalidValueException {
				//
				// If the value is null, set it to the id
				//
				if (value == null || value.toString().length() == 0) {
					return;
				}
			}
		});
		//
		//
		//
		this.textDescription.setNullRepresentation("");
	}
	
	protected void initializeButton() {
		this.buttonSave.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				try {
					//
					// Do a commit
					//
					self.textName.commit();
					self.textId.commit();
					self.textDescription.commit();
					//
					// Should be a string, but to be safe
					//
					String id = self.textId.getValue();
					String name = self.textName.getValue();
					if (name == null || name.isEmpty()) {
						self.textName.setValue(id);
						name = id;
					}
					//
					// ID must be unique.
					// Also the Name must be unique AND not match any existing IDs
					// because user uses the NAME to identify this PDP on the browser window, not the ID.
					//
					for (PDPGroup g : self.groups) {
						for (PDP p : g.getPdps()) {
							if (p.getId().equals(id)) {
								if (self.pdp != null) {
									//
									// we are editing this pdp
									//
									continue;
								}
								throw new InvalidValueException("URL must be unique - the PDP '" + id + "' already exists in group '" + g.getName() + "'");
							}
							if (id.equals(p.getName())) {
								throw new InvalidValueException("A previous PDP with URL '" + p.getId() + "' has been given the name '" + id + 
										"'.  Please edit that PDP to change the name before creating a nwe PDP with this URL.");
							}
							if (name != null && name.length() > 0 && self.pdp == null) {
								if (p.getId().equals(name) || name.equals(p.getName())) {
									throw new InvalidValueException("Name must not be the same as another PDP's name OR another PDP's URL.");
								}
							}
						}
					}
					//
					// make sure name is NOT a URL, unless it is identical to the ID.
					// (If it is a URL, then a later PDP might be created with that URL as it's ID, which would be confusing.)
					//
					if ( ! id.equals(name)) {
						try {
							new URL(name);
							// if we get here the name is a URL but not identical to the id, which is not good
							AdminNotification.warn("The Name must not be a URL unless it is the same as the PDP URL");
							return;
						} catch (Exception e) {
							// ignore - we want to get here
						}
					}
					//
					// If we get here the inputs are ok
					//
					self.isSaved = true;
					//
					//
					//
					self.close();
				} catch (SourceException | InvalidValueException e1) {
					//
					// Vaadin will display error
					//
				}
			}
		});
	}
	
	public boolean isSaved() {
		return this.isSaved;
	}
	
	public String getPDPId() {
		return this.textId.getValue();
	}
	
	public String getPDPName() {
		return this.textName.getValue();
	}
	
	public String getPDPDescription() {
		return this.textDescription.getValue();
	}
	
	@AutoGenerated
	private VerticalLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new VerticalLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("-1px");
		mainLayout.setHeight("-1px");
		mainLayout.setMargin(true);
		mainLayout.setSpacing(true);
		
		// top-level component properties
		setWidth("-1px");
		setHeight("-1px");
		
		// textId
		textId = new TextField();
		textId.setCaption("PDP URL");
		textId.setImmediate(false);
		textId.setDescription("The URL is the ID of the PDP");
		textId.setWidth("-1px");
		textId.setHeight("-1px");
		textId.setRequired(true);
		textId.setInputPrompt("Eg. http://localhost:8080/pdp");
		mainLayout.addComponent(textId);
		
		// textName
		textName = new TextField();
		textName.setCaption("PDP Name");
		textName.setImmediate(false);
		textName.setWidth("-1px");
		textName.setHeight("-1px");
		mainLayout.addComponent(textName);
		
		// textDescription
		textDescription = new TextArea();
		textDescription.setCaption("PDP Description");
		textDescription.setImmediate(false);
		textDescription.setWidth("100.0%");
		textDescription.setHeight("-1px");
		textDescription.setNullSettingAllowed(true);
		mainLayout.addComponent(textDescription);
		mainLayout.setExpandRatio(textDescription, 1.0f);
		
		// buttonSave
		buttonSave = new Button();
		buttonSave.setCaption("Save");
		buttonSave.setImmediate(true);
		buttonSave.setWidth("-1px");
		buttonSave.setHeight("-1px");
		mainLayout.addComponent(buttonSave);
		mainLayout.setComponentAlignment(buttonSave, new Alignment(48));
		
		return mainLayout;
	}

}

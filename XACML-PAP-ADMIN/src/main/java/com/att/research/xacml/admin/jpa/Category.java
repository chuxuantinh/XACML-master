/*
 *                        AT&T - PROPRIETARY
 *          THIS FILE CONTAINS PROPRIETARY INFORMATION OF
 *        AT&T AND IS NOT TO BE DISCLOSED OR USED EXCEPT IN
 *             ACCORDANCE WITH APPLICABLE AGREEMENTS.
 *
 *          Copyright (c) 2013 AT&T Knowledge Ventures
 *              Unpublished and Not for Publication
 *                     All Rights Reserved
 */
package com.att.research.xacml.admin.jpa;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.att.research.xacml.api.Identifier;
import com.att.research.xacml.api.XACML3;
import com.att.research.xacml.std.IdentifierImpl;


/**
 * The persistent class for the Categories database table.
 * 
 */
@Entity
@Table(name="Category")
@NamedQuery(name="Category.findAll", query="SELECT c FROM Category c")
public class Category implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final char STANDARD = 'S';
	public static final char CUSTOM = 'C';

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="id")
	private int id;

	@Column(name="grouping", nullable=false, length=64)
	private String grouping;

	@Column(name="is_standard", nullable=false)
	private char isStandard;

	@Column(name="xacml_id", nullable=false, unique=true, length=255)
	private String xacmlId;
	
	@Column(name="short_name", nullable=false, length=64)
	private String shortName;
	
	//bi-directional many-to-one association to Attribute
	@OneToMany(mappedBy="categoryBean")
	private Set<Attribute> attributes = new HashSet<>();

	public Category() {
		this.xacmlId = XACML3.ID_SUBJECT_CATEGORY_ACCESS_SUBJECT.stringValue();
		this.grouping = "subject";
		this.isStandard = Category.STANDARD;
		this.shortName = "subject";
	}

	public Category(Identifier cat, String grouping, char isStandard) {
		if (cat != null) {
			this.xacmlId = cat.stringValue();
		}
		this.isStandard = isStandard;
		if (grouping != null) {
			this.grouping = grouping;
		} else {
			this.grouping = Category.extractGrouping(this.xacmlId);
		}
	}

	public Category(Identifier cat, String grouping) {
		this(cat, grouping, Category.STANDARD);
	}

	public Category(Identifier cat, char standard) {
		this(cat, null, standard);
	}

	public Category(Identifier cat) {
		this(cat, Category.STANDARD);
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getGrouping() {
		return this.grouping;
	}

	public void setGrouping(String grouping) {
		this.grouping = grouping;
	}

	public char getIsStandard() {
		return this.isStandard;
	}

	public void setIsStandard(char isStandard) {
		this.isStandard = isStandard;
	}

	public String getXacmlId() {
		return this.xacmlId;
	}

	public void setXacmlId(String xacmlId) {
		this.xacmlId = xacmlId;
	}

	public String getShortName() {
		return this.shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public Set<Attribute> getAttributes() {
		return this.attributes;
	}

	public void setAttributes(Set<Attribute> attributes) {
		this.attributes = attributes;
	}

	public Attribute addAttribute(Attribute attribute) {
		getAttributes().add(attribute);
		attribute.setCategoryBean(this);

		return attribute;
	}

	public Attribute removeAttribute(Attribute attribute) {
		getAttributes().remove(attribute);
		attribute.setCategoryBean(null);

		return attribute;
	}

	@Transient
	public boolean isStandard() {
		return (this.isStandard == Category.STANDARD);
	}
	
	@Transient
	public boolean isCustom() {
		return (this.isStandard == Category.CUSTOM);
	}
	
	@Transient
	public static String	extractGrouping(String xacmlId) {
		if (xacmlId == null) {
			return null;
		}
		if (xacmlId.matches(".*:attribute\\-category:.*")) {
			String[] parts = xacmlId.split("[:]");
			if (parts != null && parts.length > 0) {
				return parts[parts.length - 1];
			}
		} else if (xacmlId.matches(".*:[a-zA-Z]+[\\-]category:.*")) {
			String[] parts = xacmlId.split("[:]");
			if (parts != null && parts.length > 0) {
				for (String part : parts) {
					int index = part.indexOf("-category");
					if (index > 0) {
						return part.substring(0, index);
					}
				}
			}
		}
		return null;
	}
	
	@Transient
	public Identifier getIdentifer() {
		return new IdentifierImpl(this.xacmlId);
	}

	@Transient
	@Override
	public String toString() {
		return "Category [id=" + id + ", grouping=" + grouping
				+ ", isStandard=" + isStandard + ", xacmlId=" + xacmlId
				+ ", attributes=" + attributes + "]";
	}

}
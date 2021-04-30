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
package com.att.research.xacml.std.pip.engines.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import com.att.research.xacml.api.Attribute;
import com.att.research.xacml.api.pip.PIPEngine;
import com.att.research.xacml.api.pip.PIPException;
import com.att.research.xacml.api.pip.PIPFinder;
import com.att.research.xacml.api.pip.PIPRequest;
import com.att.research.xacml.std.pip.engines.ConfigurableResolver;

/**
 * JDBCResolver is the interface used by the {@link com.att.research.xacml.std.pip.engines.jdbc.JDBCEngine} to
 * create <code>PreparedStatement</code>s for XACML attribute requests and convert the <code>ResultSet</code>
 * into XACML attributes.
 * 
 * @author car
 * @version $Revision$
 */
public interface JDBCResolver extends ConfigurableResolver {

	/**
	 * Creates a {@link java.sql.PreparedStatement} for a SQL SELECT statement that will retrieve the data
	 * used to generate the XACML AttributeValues.
	 * 
	 * @param pipEngine the {@link com.att.research.xacml.api.pip.PIPEngine} making the request for XACML AttributeValues
	 * @param pipRequest the {@link com.att.research.xacml.api.pip.PIPRequest} with the requested XACML Attributes
	 * @param pipFinder the {@link com.att.research.xacml.api.pip.PIPFinder} for finding any other XACML AttributeValues required to build the query.
	 * @param connection the {@link java.sql.Connection} needed to create the <code>PreparedStatement</code>
	 * @return a <code>PreparedStatement</code> to query the database for the required XACML AttributeValues.
	 * @throws PIPException if there is an error creating the <code>PreparedStatement</code>
	 */
	public PreparedStatement getPreparedStatement(PIPEngine pipEngine, PIPRequest pipRequest, PIPFinder pipFinder, Connection connection) throws PIPException;
	
	/**
	 * Creates a <code>List</code> of {@link com.att.research.xacml.api.Attribute}s from the given {@link java.sql.ResultSet}
	 * returned by a SQL SELECT statement.  The <code>ResultSet</code> should already be positioned at the first element to decode.
	 * The implementation may choose to decode multiple results before returning or may only decode the current result.  The implementation
	 * should leave the cursor on the last result decoded.
	 * 
	 * @param resultSet the <code>ResultSet</code> returned from a SQL SELECT statement.
	 * @return a new <code>List</code> of <code>Attribute</code>s generated by parsing the given <code>ResultSet</code> or an empty
	 * <code>List</code> if there are no results.
	 * @throws PIPException
	 */
	public List<Attribute> decodeResult(ResultSet resultSet) throws PIPException;
}

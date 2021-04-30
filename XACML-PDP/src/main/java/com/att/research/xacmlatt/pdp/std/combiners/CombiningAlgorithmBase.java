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
package com.att.research.xacmlatt.pdp.std.combiners;

import com.att.research.xacml.api.Identifier;
import com.att.research.xacmlatt.pdp.policy.CombiningAlgorithm;

public abstract class CombiningAlgorithmBase<T extends com.att.research.xacmlatt.pdp.eval.Evaluatable> implements CombiningAlgorithm<T> {
	private Identifier id;
	
	public CombiningAlgorithmBase(Identifier identifierIn) {
		this.id	= identifierIn;
	}

	@Override
	public Identifier getId() {
		return this.id;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder	= new StringBuilder("{");
		
		Object objectToDump;
		if ((objectToDump = this.getId()) != null) {
			stringBuilder.append("id=");
			stringBuilder.append(objectToDump.toString());
		}
		stringBuilder.append('}');
		return stringBuilder.toString();
	}
}

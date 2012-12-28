package com.sap.core.odata.processor.jpa.jpql;

import com.sap.core.odata.processor.jpa.jpql.api.JPQLContext;
import com.sap.core.odata.processor.jpa.jpql.api.JPQLContext.JPQLContextBuilder;
import com.sap.core.odata.processor.jpa.jpql.api.JPQLContextType;
import com.sap.core.odata.processor.jpa.jpql.api.JPQLStatement.JPQLStatementBuilder;

public abstract class JPQLBuilderFactory {
	
	public static JPQLStatementBuilder getStatementBuilder(JPQLContext context){
		switch (context.getType()) {
		case SELECT:
			JPQLStatementBuilder b = new JPQLSelectStatementBuilder(context);
			return b;
		default:
			break;
		}
		
		return null;
	}
	
	public static JPQLContextBuilder getContextBuilder(JPQLContextType contextType){
		JPQLContextBuilder contextBuilder = null;
		switch (contextType) {
		case SELECT:
			JPQLSelectContextImpl context = new JPQLSelectContextImpl();
			contextBuilder =  context.new JPQLSelectContextBuilder();
			break;
		
		default:
			break;
		}
		
		return contextBuilder;		
	}
}

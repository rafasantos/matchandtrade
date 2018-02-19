package com.matchandtrade.persistence.criteria;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.matchandtrade.persistence.common.SearchCriteria;

@Component
public class OfferQueryBuilder implements QueryBuilder {

	public enum Field implements com.matchandtrade.persistence.common.Field {
		offeredItemId("offeredItem.itemId"),
		tradeMembershipId("membership.tradeMembershipId"),
		wantedItemId("wantedItem.itemId");
		
		private String alias;

		private Field(String alias) {
			this.alias = alias;
		}
		
		@Override
		public String alias() {
			return alias;
		}
	}
	
	@Autowired
	private EntityManager entityManager;
	
    private static final String BASIC_HQL = 
    	  " FROM TradeMembershipEntity AS membership"
    	+ " INNER JOIN membership.offers AS offer"
    	+ " INNER JOIN membership.user AS user"
    	+ " INNER JOIN offer.offeredItem AS offeredItem"
    	+ " INNER JOIN offer.wantedItem AS wantedItem";

    @Override
    public Query buildCountQuery(SearchCriteria searchCriteria) {
    	StringBuilder hql = new StringBuilder("SELECT COUNT(*) " + BASIC_HQL);
    	return QueryBuilderUtil.parameterizeQuery(searchCriteria.getCriteria(), hql, entityManager);
    }

    @Override
	public Query buildSearchQuery(SearchCriteria searchCriteria) {
    	StringBuilder hql = new StringBuilder("SELECT offer " + BASIC_HQL);
		return QueryBuilderUtil.parameterizeQuery(searchCriteria.getCriteria(), hql, entityManager);
	}

}
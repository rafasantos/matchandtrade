package com.matchandtrade.rest.service;

import com.matchandtrade.persistence.common.Pagination;
import com.matchandtrade.persistence.common.SearchCriteria;
import com.matchandtrade.persistence.common.SearchResult;
import com.matchandtrade.persistence.common.Sort;
import com.matchandtrade.persistence.criteria.TradeQueryBuilder;
import com.matchandtrade.persistence.entity.MembershipEntity;
import com.matchandtrade.persistence.entity.TradeEntity;
import com.matchandtrade.persistence.entity.UserEntity;
import com.matchandtrade.persistence.facade.MembershipRepositoryFacade;
import com.matchandtrade.persistence.facade.TradeRepositoryFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

import static com.matchandtrade.persistence.common.Criterion.Restriction.EQUALS_IGNORE_CASE;
import static com.matchandtrade.persistence.common.Criterion.Restriction.NOT_EQUALS;
import static com.matchandtrade.persistence.criteria.TradeQueryBuilder.Field.NAME;
import static com.matchandtrade.persistence.criteria.TradeQueryBuilder.Field.TRADE_ID;

@Component
public class TradeService {

	@Autowired
	private SearchService<TradeEntity> searchService;
	@Autowired
	private TradeRepositoryFacade tradeRepository;
	@Autowired
	private MembershipRepositoryFacade membershipRepository;
	@Autowired
	private TradeResultService tradeResultService;
	
	@Transactional
	public void create(TradeEntity tradeEntity, UserEntity tradeOwner) {
		tradeEntity.setState(TradeEntity.State.SUBMITTING_ARTICLES);
		tradeRepository.save(tradeEntity);
		// Make authenticated user the owner of the trade
		MembershipEntity membershipEntity = new MembershipEntity();
		membershipEntity.setTrade(tradeEntity);
		membershipEntity.setUser(tradeOwner);
		membershipEntity.setType(MembershipEntity.Type.OWNER);
		membershipRepository.save(membershipEntity);
	}

	public boolean isNameUniqueExceptForTradeId(String name, Integer tradeId) {
		SearchCriteria searchCriteriaUniqueName = new SearchCriteria(new Pagination(1,1));
		searchCriteriaUniqueName.addCriterion(NAME, name);
		searchCriteriaUniqueName.addCriterion(TRADE_ID, tradeId, NOT_EQUALS);
		SearchResult<TradeEntity> searchResult = searchService.search(searchCriteriaUniqueName, TradeQueryBuilder.class);
		return searchResult.isEmpty();
	}

	public boolean isNameUnique(String name) {
		SearchCriteria searchCriteria = new SearchCriteria(new Pagination());
		searchCriteria.addCriterion(NAME, name, EQUALS_IGNORE_CASE);
		SearchResult<TradeEntity> searchResult = searchService.search(searchCriteria, TradeQueryBuilder.class);
		return searchResult.isEmpty();
	}

	public void delete(Integer tradeId) {
		tradeRepository.delete(tradeId);
	}

	public TradeEntity find(Integer tradeId) {
		return tradeRepository.find(tradeId);
	}

	public SearchResult<TradeEntity> search(Integer pageNumber, Integer pageSize) {
		SearchCriteria searchCriteria = new SearchCriteria(new Pagination(pageNumber, pageSize));
		searchCriteria.addSort(new Sort(TRADE_ID, Sort.Type.DESC));
		return searchService.search(searchCriteria, TradeQueryBuilder.class);
	}

	@Transactional
	public void update(TradeEntity tradeEntity) {
		tradeRepository.save(tradeEntity);
		if (TradeEntity.State.GENERATE_RESULTS == tradeEntity.getState()) {
			tradeResultService.generateResults(tradeEntity.getTradeId());
		}
	}

}

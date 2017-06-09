package com.matchandtrade.persistence.facade;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.matchandtrade.persistence.common.SearchCriteria;
import com.matchandtrade.persistence.common.SearchResult;
import com.matchandtrade.persistence.criteria.TradeQueryBuilderJavax;
import com.matchandtrade.persistence.entity.TradeEntity;
import com.matchandtrade.persistence.entity.TradeMembershipEntity;
import com.matchandtrade.persistence.repository.TradeMembershipRepository;
import com.matchandtrade.persistence.repository.TradeRepository;

@Repository
public class TradeRepositoryFacade {

	@Autowired
	private QueryableRepositoryJavax<TradeEntity> queryableTradeRepository;
	@Autowired
	private TradeMembershipRepository tradeMembershipRepository;
	@Autowired
	private TradeRepository tradeRepository;
	@Autowired
	private TradeQueryBuilderJavax tradeQueryBuilder;

	@Transactional
	public void delete(Integer tradeId) {
		List<TradeMembershipEntity> tradeMemberships = tradeMembershipRepository.findByTrade_TradeId(tradeId);
		tradeMembershipRepository.delete(tradeMemberships);
		tradeRepository.delete(tradeId);
	}

	public TradeEntity get(Integer tradeId) {
		return tradeRepository.findOne(tradeId);
	}
	
	public void save(TradeEntity entity) {
		tradeRepository.save(entity);
	}

	public SearchResult<TradeEntity> search(SearchCriteria searchCriteria) {
		return queryableTradeRepository.query(searchCriteria, tradeQueryBuilder);
	}
}

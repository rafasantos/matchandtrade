package com.matchandtrade.rest.service;

import com.matchandtrade.persistence.entity.ArticleEntity;
import com.matchandtrade.persistence.entity.MembershipEntity;
import com.matchandtrade.persistence.facade.ArticleRepositoryFacade;
import com.matchandtrade.persistence.facade.MembershipRepositoryFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class MembershipArticleService {

	@Autowired
	private ArticleRepositoryFacade articleRepositoryFacade;
	@Autowired
	private MembershipRepositoryFacade membershipRepositoryFacade;

	@Transactional
	public void create(Integer membershipId, Integer articleId) {
		MembershipEntity membership = membershipRepositoryFacade.get(membershipId);
		ArticleEntity article = articleRepositoryFacade.get(articleId);
		membership.getArticles().add(article);
		membershipRepositoryFacade.save(membership);
	}

}

package com.matchandtrade.rest.service;

import com.matchandtrade.persistence.common.Pagination;
import com.matchandtrade.persistence.common.SearchCriteria;
import com.matchandtrade.persistence.common.SearchResult;
import com.matchandtrade.persistence.common.Sort;
import com.matchandtrade.persistence.entity.ArticleEntity;
import com.matchandtrade.persistence.entity.MembershipEntity;
import com.matchandtrade.persistence.entity.TradeEntity;
import com.matchandtrade.persistence.entity.UserEntity;
import com.matchandtrade.persistence.repository.ArticleRepository;
import com.matchandtrade.test.DefaultTestingConfiguration;
import com.matchandtrade.test.StringRandom;
import com.matchandtrade.test.helper.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import static com.matchandtrade.rest.service.SearchRecipeService.Field.*;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DefaultTestingConfiguration
public class SearchRecipeServiceIT {
	@Autowired
	private UserHelper userHelper;
	@Autowired
	private ArticleHelper articleHelper;
	@Autowired
	private ArticleRepository articleRepository;
	@Autowired
	private TradeHelper tradeHelper;
	@Autowired
	private ListingHelper listingHelper;
	@Autowired
	private MembershipHelper membershipHelper;
	@Autowired
	private SearchHelper searchHelper;
	@Autowired
	private SearchRecipeService fixture;


	@Test
	public void post_When_RecipeIsArticleAndCriteriaIsUserId_Then_ReturnAllArticlesForThatUser() {
		UserEntity user = userHelper.createPersistedEntity();
		ArticleEntity article1 = articleHelper.createPersistedEntity(user);
		ArticleEntity article2 = articleHelper.createPersistedEntity(user);
		ArticleEntity article3 = articleHelper.createPersistedEntity(user);

		SearchCriteria searchCriteria = new SearchCriteria(new Pagination());
		searchCriteria.addCriterion(USER_ID, user.getUserId());
		SearchResult<ArticleEntity> searchResult = fixture.search(searchCriteria);

		assertTrue(searchResult.getResultList().contains(article1));
		assertTrue(searchResult.getResultList().contains(article2));
		assertTrue(searchResult.getResultList().contains(article3));
		assertEquals(3, searchResult.getPagination().getTotal());
	}

	@Test
	public void post_When_RecipeIsArticleAndCriteriaIsUserIdAndSortIsName_Then_ReturnAllArticlesForThatUserSortedByName() {
		UserEntity user = userHelper.createPersistedEntity();
		ArticleEntity article1 = articleHelper.createPersistedEntity(user);
		article1.setName("Bravo");
		articleRepository.save(article1);
		ArticleEntity article2 = articleHelper.createPersistedEntity(user);
		article2.setName("Alpha");
		articleRepository.save(article2);
		ArticleEntity article3 = articleHelper.createPersistedEntity(user);
		article3.setName("Charlie");
		articleRepository.save(article3);

		SearchCriteria searchCriteria = new SearchCriteria(new Pagination());
		searchCriteria.addCriterion(USER_ID, user.getUserId());
		searchCriteria.addSort(new Sort(ARTICLE_NAME, Sort.Type.ASC));
		SearchResult<ArticleEntity> searchResult = fixture.search(searchCriteria);

		assertEquals(article2.getName(), searchResult.getResultList().get(0).getName());
		assertEquals(article1.getName(), searchResult.getResultList().get(1).getName());
		assertEquals(article3.getName(), searchResult.getResultList().get(2).getName());
		assertEquals(3, searchResult.getPagination().getTotal());
	}

	@Test
	public void post_When_RecipeIsArticleAndCriteriaIsName_Then_ReturnAllArticlesWithThatName() {
		String name = StringRandom.nextAlphanumeric(150);
		ArticleEntity article1 = articleHelper.createPersistedEntity(name);
		ArticleEntity article2 = articleHelper.createPersistedEntity(name);

		SearchCriteria searchCriteria = new SearchCriteria(new Pagination());
		searchCriteria.addCriterion(ARTICLE_NAME, name);
		SearchResult<ArticleEntity> searchResult = fixture.search(searchCriteria);

		assertTrue(searchResult.getResultList().contains(article1));
		assertTrue(searchResult.getResultList().contains(article2));
		assertEquals(2, searchResult.getPagination().getTotal());
	}

	@Test
	public void post_When_RecipeIsArticleAndCriteriaIsUserIdAndArticleId_Then_ReturnTheArticleIfBelongsToThatUser() {
		UserEntity user = userHelper.createPersistedEntity();
		ArticleEntity article = articleHelper.createPersistedEntity(user);

		SearchCriteria searchCriteria = new SearchCriteria(new Pagination());
		searchCriteria.addCriterion(USER_ID, user.getUserId());
		searchCriteria.addCriterion(ARTICLE_ID, article.getArticleId());
		SearchResult<ArticleEntity> searchResult = fixture.search(searchCriteria);

		assertTrue(searchResult.getResultList().contains(article));
		assertEquals(1, searchResult.getPagination().getTotal());
	}

	@Test
	public void post_When_RecipeIsArticleAndCriteriaIsUserIdAndArticleIdThatDoesNotBelongToUser_Then_ReturnNoResults() {
		UserEntity user = userHelper.createPersistedEntity();
		ArticleEntity article = articleHelper.createPersistedEntity();

		SearchCriteria searchCriteria = new SearchCriteria(new Pagination());
		searchCriteria.addCriterion(USER_ID, user.getUserId());
		searchCriteria.addCriterion(ARTICLE_ID, article.getArticleId());
		SearchResult<ArticleEntity> searchResult = fixture.search(searchCriteria);

		assertTrue(searchResult.getResultList().isEmpty());
		assertEquals(0, searchResult.getPagination().getTotal());
	}

	@Test
	public void post_When_RecipeIsArticleAndCriteriaIsTradeId_Then_ReturnAllArticlesThatAreAssociatedToThatTrade() {
		UserEntity user = userHelper.createPersistedEntity();
		ArticleEntity article = articleHelper.createPersistedEntity(user);
		TradeEntity trade = tradeHelper.createPersistedEntity(user);
		MembershipEntity membership = searchHelper.findMembership(user, trade);
		listingHelper.createPersisted(article.getArticleId(), membership.getMembershipId());

		SearchCriteria searchCriteria = new SearchCriteria(new Pagination());
		searchCriteria.addCriterion(TRADE_ID, trade.getTradeId());
		SearchResult<ArticleEntity> searchResult = fixture.search(searchCriteria);

		assertTrue(searchResult.getResultList().contains(article));
		assertEquals(1, searchResult.getPagination().getTotal());
	}

	@Test
	public void post_When_RecipeIsArticleAndPageSizeIs2_Then_ReturnOnly2Articles() {
		UserEntity user = userHelper.createPersistedEntity();
		ArticleEntity article1 = articleHelper.createPersistedEntity(user);
		ArticleEntity article2 = articleHelper.createPersistedEntity(user);
		ArticleEntity article3 = articleHelper.createPersistedEntity(user);

		SearchCriteria searchCriteria = new SearchCriteria(new Pagination(1, 2));
		searchCriteria.addCriterion(USER_ID, user.getUserId());
		SearchResult<ArticleEntity> searchResult = fixture.search(searchCriteria);

		assertTrue(searchResult.getResultList().contains(article1));
		assertTrue(searchResult.getResultList().contains(article2));
		assertFalse(searchResult.getResultList().contains(article3));
		assertEquals(3, searchResult.getPagination().getTotal());
	}

	@Test
	public void post_When_RecipeIsArticleAndPageNumberIs2_Then_ReturnTheSecondPage() {
		UserEntity user = userHelper.createPersistedEntity();
		ArticleEntity article1 = articleHelper.createPersistedEntity(user);
		ArticleEntity article2 = articleHelper.createPersistedEntity(user);
		ArticleEntity article3 = articleHelper.createPersistedEntity(user);

		SearchCriteria searchCriteria = new SearchCriteria(new Pagination(2, 2));
		searchCriteria.addCriterion(USER_ID, user.getUserId());
		SearchResult<ArticleEntity> searchResult = fixture.search(searchCriteria);

		assertFalse(searchResult.getResultList().contains(article1));
		assertFalse(searchResult.getResultList().contains(article2));
		assertTrue(searchResult.getResultList().contains(article3));
		assertEquals(3, searchResult.getPagination().getTotal());
	}

}

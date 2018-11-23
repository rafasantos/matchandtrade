package com.matchandtrade.rest.v1.validator;

import com.matchandtrade.persistence.common.Pagination;
import com.matchandtrade.persistence.common.SearchCriteria;
import com.matchandtrade.persistence.common.SearchResult;
import com.matchandtrade.persistence.criteria.MembershipQueryBuilder;
import com.matchandtrade.persistence.entity.ArticleEntity;
import com.matchandtrade.persistence.entity.MembershipEntity;
import com.matchandtrade.persistence.facade.ArticleRepositoryFacade;
import com.matchandtrade.rest.RestException;
import com.matchandtrade.rest.service.ListingService;
import com.matchandtrade.rest.service.SearchService;
import com.matchandtrade.rest.v1.json.ListingJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class ListingValidator {

	@Autowired
	ArticleRepositoryFacade articleRepositoryFacade;
	@Autowired
	ListingService listingService;

	public void validateDelete(Integer userId, ListingJson listing) {
		verifyThatUserOwnsMembership(userId, listing.getMembershipId());
		verifyThatUserOwnsArticle(userId, listing.getArticleId());
	}

	public void validatePost(Integer userId, ListingJson listing) {
		verifyThatMembershipIdIsNotNull(listing.getMembershipId());
		verifyThatArticleIdIsNotNull(listing.getArticleId());
		verifyThatUserOwnsMembership(userId, listing.getMembershipId());
		verifyThatUserOwnsArticle(userId, listing.getArticleId());
	}

	private void verifyThatMembershipIdIsNotNull(Integer membershipId) {
		if (membershipId == null) {
			throw new RestException(HttpStatus.BAD_REQUEST, "Listing.membershipId cannot be null");
		}
	}

	private void verifyThatArticleIdIsNotNull(Integer articleId) {
		if (articleId == null) {
			throw new RestException(HttpStatus.BAD_REQUEST, "Listing.articleId cannot be null");
		}
	}

	private void verifyThatUserOwnsArticle(Integer userId, Integer articleId) {
		ArticleEntity article = articleRepositoryFacade.findByUserIdAndArticleId(userId, articleId);
		if (article == null) {
			throw new RestException(HttpStatus.BAD_REQUEST, String.format("User.userId: %s does not own Article.articleId: %s", userId, articleId));
		}
	}

	private void verifyThatUserOwnsMembership(Integer userId, Integer membershipId) {
		SearchResult<MembershipEntity> searchResult = listingService.findMembershipByUserIdAndMembershpiId(userId, membershipId);
		if (searchResult.isEmpty()) {
			throw new RestException(HttpStatus.BAD_REQUEST, String.format("User.userId: %s does not own Membership.membershipId: %s", userId, membershipId));
		}
	}

}

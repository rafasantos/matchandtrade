package com.matchandtrade.rest.v1.validator;

import com.matchandtrade.rest.RestException;
import com.matchandtrade.rest.v1.json.search.Recipe;
import com.matchandtrade.rest.v1.json.search.SearchCriteriaJson;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import static org.junit.Assert.assertEquals;

public class SearchValidatorUT {

	private static final String ARTICLES_RECIPE = "ARTICLES";

	@Test(expected = RestException.class)
	public void validatePost_When_SearchCriteriaHasNoRecipe_Then_BadRequest() {
		SearchCriteriaJson given = new SearchCriteriaJson();
		try {
			SearchValidator.validatePost(given, 1, 1);
		} catch (RestException e) {
			assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
			assertEquals("Recipe is mandatory", e.getDescription());
			throw e;
		}
	}

	@Test(expected = RestException.class)
	public void validatePost_When_SearchCriteriaHasRecipeButNoCriteria_Then_BadRequest() {
		SearchCriteriaJson given = new SearchCriteriaJson();
		given.setRecipe(ARTICLES_RECIPE);
		try {
			SearchValidator.validatePost(given, 1, 1);
		} catch (RestException e) {
			assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
			assertEquals("Criteria is mandatory", e.getDescription());
			throw e;
		}
	}

	@Test
	public void validatePost_When_SearchCriteriaHasRecipeAndCriteria_Then_Succeeds() {
		SearchCriteriaJson given = new SearchCriteriaJson();
		given.setRecipe(ARTICLES_RECIPE);
		given.addCriterion("Article.articleId", 1);
		SearchValidator.validatePost(given, 1, 1);
	}

	@Test
	public void validatePost_When_SearchCriteriaHasRecipeAndInvalidCriteria_Then_BadRequest() {
		SearchCriteriaJson given = new SearchCriteriaJson();
		given.setRecipe(ARTICLES_RECIPE);
		given.addCriterion("Invalid field", 1);
		try {
			SearchValidator.validatePost(given, 1, 1);
		} catch (RestException e) {
			assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
			assertEquals("Invalid field: Invalid field", e.getDescription());
		}

	}

	@Test(expected = RestException.class)
	public void validatePost_When_SearchCriteriaHasInvalidRecipe_Then_BadRequest() {
		SearchCriteriaJson given = new SearchCriteriaJson();
		given.setRecipe("Invalid recipe :p");
		given.addCriterion("article.articleId", 1);
		try {
			SearchValidator.validatePost(given, 1, 1);
		} catch (RestException e) {
			assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
			assertEquals("Invalid recipe", e.getDescription());
			throw e;
		}
	}
}
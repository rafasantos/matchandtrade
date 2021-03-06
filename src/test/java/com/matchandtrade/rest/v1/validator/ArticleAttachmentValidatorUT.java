package com.matchandtrade.rest.v1.validator;

import com.matchandtrade.persistence.common.Pagination;
import com.matchandtrade.persistence.common.SearchResult;
import com.matchandtrade.persistence.entity.ArticleEntity;
import com.matchandtrade.persistence.entity.AttachmentEntity;
import com.matchandtrade.rest.RestException;
import com.matchandtrade.rest.service.ArticleAttachmentService;
import com.matchandtrade.rest.service.ArticleService;
import com.matchandtrade.rest.service.AttachmentService;
import com.matchandtrade.test.helper.SearchHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RunWith(MockitoJUnitRunner.class)
public class ArticleAttachmentValidatorUT {
	private ArticleAttachmentValidator fixture = new ArticleAttachmentValidator();
	@Mock
	private ArticleService mockedArticleService;
	@Mock
	private AttachmentService mockedAttachmentService;
	@Mock
	private ArticleAttachmentService mockedArticleAttachmentService;
	private ArticleEntity existingArticle;
	private AttachmentEntity existingAttachment;

	@Before
	public void before() {
		existingArticle = new ArticleEntity();
		existingArticle.setArticleId(1);
		when(mockedArticleService.findByArticleId(existingArticle.getArticleId())).thenReturn(existingArticle);

		existingAttachment = new AttachmentEntity();
		existingAttachment.setAttachmentId(UUID.randomUUID());
		when(mockedAttachmentService.findByAttachmentId(existingAttachment.getAttachmentId())).thenReturn(existingAttachment);

		fixture.articleService = mockedArticleService;
		fixture.attachmentService = mockedAttachmentService;
		fixture.articleAttachmentService= mockedArticleAttachmentService;
	}

	@Test(expected = RestException.class)
	public void validatePut_When_ArticleDoesNotExist_Then_NotFound() {
		try {
			fixture.validatePut(-1, existingAttachment.getAttachmentId());
		} catch (RestException e) {
			verifyThatArticleIsNotFound(e);
			return;
		}
	}

	@Test(expected = RestException.class)
	public void validatePut_When_AttachmentDoesNotExist_Then_NotFound() {
		try {
			fixture.validatePut(existingArticle.getArticleId(), UUID.randomUUID());
		} catch (RestException e) {
			assertEquals(NOT_FOUND, e.getHttpStatus());
			assertEquals("Attachment.attachmentId was not found", e.getDescription());
			throw e;
		}
	}

	@Test(expected = RestException.class)
	public void validatePut_When_ArticleHas3Attachments_Then_BadRequest() {
		SearchResult<AttachmentEntity> mockedSearchResult = new SearchResult<>(emptyList(), new Pagination(1, 10, 3L));
		when(mockedArticleAttachmentService.findByArticleId(1)).thenReturn(mockedSearchResult);
		try {
			fixture.validatePut(1, existingAttachment.getAttachmentId());
		} catch (RestException e) {
			assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
			assertEquals("Articles cannot have more than 3 Attachments", e.getDescription());
			throw e;
		}
	}

	@Test
	public void validatePut_When_ArticleAndAttachmentExist_Then_Succeeds() {
		when(mockedArticleAttachmentService.findByArticleId(1)).thenReturn(SearchHelper.buildEmptySearchResult());
		fixture.validatePut(existingArticle.getArticleId(), existingAttachment.getAttachmentId());
	}

	@Test
	public void validateGet_When_ArticleExists_Then_Succeeds() {
		fixture.validateGet(existingArticle.getArticleId());
	}

	@Test(expected = RestException.class)
	public void validateGet_When_ArticleDoesNotExist_Then_NotFound() {
		try {
			fixture.validateGet(-1);
		} catch (RestException e) {
			verifyThatArticleIsNotFound(e);
		}
	}

	private void verifyThatArticleIsNotFound(RestException e) {
		assertEquals(NOT_FOUND, e.getHttpStatus());
		assertEquals("Article.articleId was not found", e.getDescription());
		throw e;
	}
}

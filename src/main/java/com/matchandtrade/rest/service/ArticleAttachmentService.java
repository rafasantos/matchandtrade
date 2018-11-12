package com.matchandtrade.rest.service;

import com.matchandtrade.persistence.entity.ArticleEntity;
import com.matchandtrade.persistence.entity.AttachmentEntity;
import com.matchandtrade.persistence.facade.AttachmentRepositoryFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ArticleAttachmentService {
	
	@Autowired
	private AttachmentRepositoryFacade attachmentRepositoryFacade;
	@Autowired
	private AttachmentService attachmentService;
	@Autowired
	private ArticleService articleService;

	@Transactional
	public AttachmentEntity create(Integer articleId, MultipartFile multipartFile) {
		AttachmentEntity attachment = attachmentService.create(multipartFile);
		ArticleEntity article = articleService.get(articleId);
		article.getAttachments().add(attachment);
		articleService.update(article);
		return attachment;
	}

	@Transactional
	public void delete(Integer articleId, Integer attachmentId) {
		AttachmentEntity attachment = attachmentRepositoryFacade.get(attachmentId);
		ArticleEntity article = articleService.get(articleId);
		// TODO: More performative implementation, this could be slow if articles have several attachments
		if (article.getAttachments().remove(attachment)) {
			articleService.update(article);
			attachmentRepositoryFacade.delete(attachmentId);
		}
	}

}

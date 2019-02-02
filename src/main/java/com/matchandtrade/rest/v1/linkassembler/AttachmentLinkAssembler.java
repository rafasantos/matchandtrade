package com.matchandtrade.rest.v1.linkassembler;

import com.matchandtrade.persistence.common.SearchResult;
import com.matchandtrade.persistence.entity.AttachmentEntity;
import com.matchandtrade.persistence.entity.EssenceEntity;
import com.matchandtrade.rest.service.AttachmentService;
import com.matchandtrade.rest.v1.controller.AttachmentController;
import com.matchandtrade.rest.v1.json.AttachmentJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import java.util.Set;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Component
public class AttachmentLinkAssembler {
	@Autowired
	AttachmentService attachmentService;

	public void assemble(AttachmentJson json) {
		Link self = linkTo(AttachmentController.class).slash(json.getAttachmentId()).withSelfRel();
		json.add(self.getRel(), self.getHref());
		assembleEssenceLinks(json);
	}

	public void assemble(SearchResult<AttachmentJson> searchResult) {
		for (AttachmentJson json : searchResult.getResultList()) {
			assemble(json);
		}
	}

	private void assembleEssenceLinks(AttachmentJson json) {
		AttachmentEntity attachment = attachmentService.findByAttachmentId(json.getAttachmentId());
		Set<EssenceEntity> essences = attachment.getEssences();
		for (EssenceEntity essence : essences) {
			switch (essence.getType()) {
				case ORIGINAL:
					json.add("original", essence.getRelativePath());
					break;
				case THUMBNAIL:
					json.add("thumbnail", essence.getRelativePath());
					break;
				default: // No default action
			}
		}
	}
}

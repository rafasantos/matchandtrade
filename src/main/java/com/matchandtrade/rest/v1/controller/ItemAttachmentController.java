package com.matchandtrade.rest.v1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.matchandtrade.authorization.AuthorizationValidator;
import com.matchandtrade.persistence.common.SearchResult;
import com.matchandtrade.persistence.entity.AttachmentEntity;
import com.matchandtrade.rest.AuthenticationProvider;
import com.matchandtrade.rest.service.AttachmentService;
import com.matchandtrade.rest.service.ItemAttachmentService;
import com.matchandtrade.rest.v1.json.AttachmentJson;
import com.matchandtrade.rest.v1.link.AttachmentLinkAssember;
import com.matchandtrade.rest.v1.transformer.AttachmentTransformer;
import com.matchandtrade.rest.v1.validator.ItemFileValidator;

@RestController
@RequestMapping(path = "/matchandtrade-web-api/v1/trade-memberships")
public class ItemAttachmentController implements Controller {

	@Autowired
	AuthenticationProvider authenticationProvider;
	@Autowired
	private ItemFileValidator itemAttachmentValidator;
	@Autowired
	private ItemAttachmentService itemAttachmentService;
	@Autowired
	private AttachmentService attachmentService;
	@Autowired
	private AttachmentLinkAssember attachmentLinkAssembler;

	@PostMapping("/{tradeMembershipId}/items/{itemId}/attachments/{attachmentId}")
	@ResponseStatus(HttpStatus.CREATED)
	public AttachmentJson post(@PathVariable Integer tradeMembershipId, @PathVariable Integer itemId, @PathVariable Integer attachmentId) {
		// Validate request identity
		AuthorizationValidator.validateIdentity(authenticationProvider.getAuthentication());
		// Validate the request
		itemAttachmentValidator.validatePost(authenticationProvider.getAuthentication().getUser().getUserId(), tradeMembershipId, itemId);
		// Transform the request
		AttachmentEntity attachmentEntity = attachmentService.get(attachmentId);
		// Delegate to service layer
		itemAttachmentService.addAttachmentToItem(itemId, attachmentId);
		// Transform the response
		AttachmentJson response = AttachmentTransformer.transform(attachmentEntity);
		// Assemble links
		AttachmentLinkAssember.assemble(response, attachmentEntity);
		return response;
	}
	
	@DeleteMapping("/{tradeMembershipId}/items/{itemId}/attachments/{attachmentId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Integer tradeMembershipId, @PathVariable Integer itemId, @PathVariable Integer attachmentId) {
		// Validate request identity
		AuthorizationValidator.validateIdentity(authenticationProvider.getAuthentication());
		// Validate the request
		itemAttachmentValidator.validateDelete(authenticationProvider.getAuthentication().getUser().getUserId(), tradeMembershipId, itemId, attachmentId);
		// Delegate to service layer
		itemAttachmentService.deleteAttachmentFromItem(itemId, attachmentId);
	}


	@RequestMapping(path={"/{tradeMembershipId}/items/{itemId}/attachments", "/{tradeMembershipId}/items/{itemId}/attachments/"}, method=RequestMethod.GET)
	public SearchResult<AttachmentJson> get(@PathVariable Integer tradeMembershipId, @PathVariable Integer itemId, Integer _pageNumber, Integer _pageSize) {
		// Validate request identity
		AuthorizationValidator.validateIdentity(authenticationProvider.getAuthentication());
		// Validate the request
		itemAttachmentValidator.validateGet(authenticationProvider.getAuthentication().getUser().getUserId(), tradeMembershipId, _pageNumber, _pageSize);
		// Delegate to service layer
		SearchResult<AttachmentEntity> searchResult = itemAttachmentService.search(itemId, _pageNumber, _pageSize);
		// Transform the response
		SearchResult<AttachmentJson> response = AttachmentTransformer.transform(searchResult);
		// Assemble links
		attachmentLinkAssembler.assemble(response);
		return response;
	}

}
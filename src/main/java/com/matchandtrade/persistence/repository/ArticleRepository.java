package com.matchandtrade.persistence.repository;

import com.matchandtrade.persistence.entity.ArticleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ArticleRepository extends CrudRepository<ArticleEntity, Integer>{
	@Query("SELECT article" +
		" FROM UserEntity AS user" +
		" INNER JOIN user.articles AS article" +
		" WHERE user.userId = :userId AND article.articleId = :articleId")
	ArticleEntity findArticleByUserIdAndArticleId(@Param("userId")Integer userId, @Param("articleId") Integer articleId);

	Page<ArticleEntity> findAll(Pageable pageable);
}

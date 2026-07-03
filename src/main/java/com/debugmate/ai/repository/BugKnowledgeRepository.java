package com.debugmate.ai.repository;

import com.debugmate.ai.entity.BugKnowledge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BugKnowledgeRepository extends JpaRepository<BugKnowledge, Long> {
    List<BugKnowledge> findByLanguageIgnoreCase(String language);
    List<BugKnowledge> findByLanguageIgnoreCaseAndCategoryIgnoreCase(String language, String category);
}

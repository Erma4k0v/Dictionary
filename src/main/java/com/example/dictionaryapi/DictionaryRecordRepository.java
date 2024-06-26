package com.example.dictionaryapi;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DictionaryRecordRepository extends JpaRepository<DictionaryRecord, Long> {
    List<DictionaryRecord> findByDictionaryId(Long dictionaryId);
}

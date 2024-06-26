package com.example.dictionaryapi;

import java.util.List;

public interface DictionaryRecordService {
    List<DictionaryRecord> getAllRecords(String dictionaryName);
    DictionaryRecord createRecord(String dictionaryName, DictionaryRecord record);
    DictionaryRecord getRecordById(String dictionaryName, Long id);
    DictionaryRecord updateRecord(String dictionaryName, Long id, DictionaryRecord record);
    void deleteRecord(String dictionaryName, Long id);
}

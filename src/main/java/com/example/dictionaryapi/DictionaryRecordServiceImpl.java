package com.example.dictionaryapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DictionaryRecordServiceImpl implements DictionaryRecordService {

    @Autowired
    private DictionaryRecordRepository dictionaryRecordRepository;

    @Autowired
    private DictionaryRepository dictionaryRepository;

    @Override
    public List<DictionaryRecord> getAllRecords(String dictionaryName) {
        Dictionary dictionary = dictionaryRepository.findByName(dictionaryName)
                .orElseThrow(() -> new DictionaryController.ResourceNotFoundException("Dictionary not found"));
        return dictionaryRecordRepository.findByDictionaryId(dictionary.getId());
    }

    @Override
    public DictionaryRecord createRecord(String dictionaryName, DictionaryRecord record) {
        Dictionary dictionary = dictionaryRepository.findByName(dictionaryName)
                .orElseThrow(() -> new DictionaryController.ResourceNotFoundException("Dictionary not found"));

        // Validate the record against the dictionary structure
        if (!isValidRecord(record.getFields(), dictionary.getStructure())) {
            throw new IllegalArgumentException("Record does not match dictionary structure");
        }

        record.setDictionary(dictionary);
        return dictionaryRecordRepository.save(record);
    }

    @Override
    public DictionaryRecord updateRecord(String dictionaryName, Long id, DictionaryRecord updatedRecord) {
        Dictionary dictionary = dictionaryRepository.findByName(dictionaryName)
                .orElseThrow(() -> new DictionaryController.ResourceNotFoundException("Dictionary not found"));

        DictionaryRecord existingRecord = dictionaryRecordRepository.findById(id)
                .orElseThrow(() -> new DictionaryController.ResourceNotFoundException("Record not found"));

        // Validate the record against the dictionary structure
        if (!isValidRecord(updatedRecord.getFields(), dictionary.getStructure())) {
            throw new IllegalArgumentException("Record does not match dictionary structure");
        }

        existingRecord.setFields(updatedRecord.getFields());
        return dictionaryRecordRepository.save(existingRecord);
    }

    @Override
    public DictionaryRecord getRecordById(String dictionaryName, Long id) {
        Dictionary dictionary = dictionaryRepository.findByName(dictionaryName)
                .orElseThrow(() -> new DictionaryController.ResourceNotFoundException("Dictionary not found"));

        return dictionaryRecordRepository.findById(id)
                .orElseThrow(() -> new DictionaryController.ResourceNotFoundException("Record not found"));
    }

    @Override
    public void deleteRecord(String dictionaryName, Long id) {
        Dictionary dictionary = dictionaryRepository.findByName(dictionaryName)
                .orElseThrow(() -> new DictionaryController.ResourceNotFoundException("Dictionary not found"));

        DictionaryRecord record = dictionaryRecordRepository.findById(id)
                .orElseThrow(() -> new DictionaryController.ResourceNotFoundException("Record not found"));

        dictionaryRecordRepository.delete(record);
    }

    private boolean isValidRecord(Map<String, String> recordFields, String structure) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode structureNode = objectMapper.readTree(structure);
            JsonNode fields = structureNode.get("fields");

            for (JsonNode field : fields) {
                String fieldName = field.get("name").asText();
                String fieldType = field.get("type").asText();

                if (recordFields.containsKey(fieldName)) {
                    String value = recordFields.get(fieldName);
                    switch (fieldType) {
                        case "string":
                            // Проверка типа строки не нужна, так как все значения в Map<String, String> уже строки
                            break;
                        case "number":
                            if (!isNumber(value)) {
                                return false;
                            }
                            break;
                        case "boolean":
                            if (!isBoolean(value)) {
                                return false;
                            }
                            break;
                        default:
                            return false;
                    }
                } else {
                    return false;
                }
            }
        } catch (JsonProcessingException e) {
            return false;
        }
        return true;
    }

    private boolean isNumber(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isBoolean(String value) {
        return value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false");
    }
}

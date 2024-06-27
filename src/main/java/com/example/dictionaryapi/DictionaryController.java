package com.example.dictionaryapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dictionaries")
public class DictionaryController {
    @Autowired
    private DictionaryRepository dictionaryRepository;

    @Autowired
    private DictionaryRecordRepository dictionaryRecordRepository;

    @GetMapping("/dictionary")
    public List<Dictionary> getAllDictionaries() {
        return dictionaryRepository.findAll();
    }

    @GetMapping("/dictionary/{id}")
    public Dictionary getDictionaryById(@PathVariable Long id) {
        return dictionaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dictionary not found"));
    }

    @PostMapping("/dictionary")
    @ResponseStatus(HttpStatus.CREATED)
    public Dictionary createDictionary(@RequestBody Map<String, Object> request) {
        String name = (String) request.get("name");
        String structureJson = (String) request.get("structure");

        // Validate and save the dictionary
        Dictionary dictionary = new Dictionary();
        dictionary.setName(name);
        dictionary.setStructure(structureJson);

        return dictionaryRepository.save(dictionary);
    }

    @PostMapping("/dictionary/{name}/records")
    @ResponseStatus(HttpStatus.CREATED)
    public DictionaryRecord addRecord(@PathVariable String name, @RequestBody Map<String, Object> recordFields) {
        Dictionary dictionary = dictionaryRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Dictionary not found"));

        // Validate the record against the dictionary structure
        if (!isValidRecord(recordFields, dictionary.getStructure())) {
            throw new IllegalArgumentException("Record does not match dictionary structure");
        }

        DictionaryRecord record = new DictionaryRecord();
        record.setDictionary(dictionary);
        record.setFields(recordFields.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().toString())));
        return dictionaryRecordRepository.save(record);
    }

    @DeleteMapping("/dictionary/{name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDictionaryByName(@PathVariable String name) {
        Dictionary dictionary = dictionaryRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Dictionary not found with name: " + name));

        List<DictionaryRecord> records = dictionaryRecordRepository.findByDictionaryId(dictionary.getId());
        dictionaryRecordRepository.deleteAll(records);
        dictionaryRepository.delete(dictionary);
    }

    @PutMapping("/dictionary/{name}/records/{id}")
    public DictionaryRecord updateRecord(@PathVariable String name, @PathVariable Long id, @RequestBody Map<String, Object> recordFields) {
        Dictionary dictionary = dictionaryRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Dictionary not found"));

        DictionaryRecord existingRecord = dictionaryRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found"));

        // Validate the record against the dictionary structure (optional)
        if (!isValidRecord(recordFields, dictionary.getStructure())) {
            throw new IllegalArgumentException("Updated record does not match dictionary structure");
        }

        // Update the existing record
        existingRecord.setFields(recordFields.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().toString())));

        return dictionaryRecordRepository.save(existingRecord);
    }

    private boolean isValidRecord(Map<String, Object> recordFields, String structure) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode structureNode = objectMapper.readTree(structure);
            JsonNode fields = structureNode.get("fields");

            for (JsonNode field : fields) {
                String fieldName = field.get("name").asText();
                String fieldType = field.get("type").asText();

                if (recordFields.containsKey(fieldName)) {
                    Object value = recordFields.get(fieldName);
                    switch (fieldType) {
                        case "string":
                            if (!(value instanceof String)) {
                                return false;
                            }
                            break;
                        case "number":
                            if (!(value instanceof Number)) {
                                return false;
                            }
                            break;
                        case "boolean":
                            if (!(value instanceof Boolean)) {
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

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }
}

package com.example.dictionaryapi;

import com.example.dictionaryapi.Dictionary;
import com.example.dictionaryapi.DictionaryRecord;
import com.example.dictionaryapi.DictionaryRecordRepository;
import com.example.dictionaryapi.DictionaryRepository;
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

    @GetMapping
    public List<Dictionary> getAllDictionaries() {
        return dictionaryRepository.findAll();
    }

    @GetMapping("/{id}")
    public Dictionary getDictionaryById(@PathVariable Long id) {
        return dictionaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dictionary not found"));
    }

    @PostMapping("/{name}/records")
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

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDictionary(@PathVariable Long id) {
        Optional<Dictionary> dictionary = dictionaryRepository.findById(id);
        if (dictionary.isPresent()) {
            if (!dictionary.get().getRecords().isEmpty()) {
                throw new IllegalStateException("Cannot delete dictionary with associated records");
            }
            dictionaryRepository.delete(dictionary.get());
        } else {
            throw new ResourceNotFoundException("Dictionary not found with id: " + id);
        }
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

package com.example.dictionaryapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dictionaries")
public class DictionaryRecordController {

    @Autowired
    private DictionaryRecordService recordService;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/{name}/records")
    public List<DictionaryRecord> getAllRecords(@PathVariable String name) {
        return recordService.getAllRecords(name);
    }

    @PostMapping("/{name}/createRecord") // Изменили маппинг для разрешения конфликта
    public DictionaryRecord createRecord(@PathVariable String name, @RequestBody String requestBody) throws JsonProcessingException {
        DictionaryRecord record = objectMapper.readValue(requestBody, DictionaryRecord.class);
        return recordService.createRecord(name, record);
    }

    @PutMapping("/{name}/records/{id}")
    public DictionaryRecord updateRecord(@PathVariable String name, @PathVariable Long id, @RequestBody String requestBody) throws JsonProcessingException {
        DictionaryRecord record = objectMapper.readValue(requestBody, DictionaryRecord.class);
        return recordService.updateRecord(name, id, record);
    }

    @GetMapping("/{name}/records/{id}")
    public DictionaryRecord getRecordById(@PathVariable String name, @PathVariable Long id) {
        return recordService.getRecordById(name, id);
    }

    @DeleteMapping("/{name}/records/{id}")
    public void deleteRecord(@PathVariable String name, @PathVariable Long id) {
        recordService.deleteRecord(name, id);
    }
}


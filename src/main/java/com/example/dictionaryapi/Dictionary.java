package com.example.dictionaryapi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

@Entity
public class Dictionary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Column(nullable = false, columnDefinition = "nvarchar(max)")
    private String structure;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dictionary")
    @JsonIgnore
    private List<DictionaryRecord> records;

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStructure() {
        return structure;
    }

    public void setStructure(String structure) {
        this.structure = structure;
    }

    public List<DictionaryRecord> getRecords() {
        return records;
    }

    public void setRecords(List<DictionaryRecord> records) {
        this.records = records;
    }
}

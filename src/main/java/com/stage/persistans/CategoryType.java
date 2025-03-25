package com.stage.persistans;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class CategoryType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String categoryType;
    @ManyToMany(fetch = FetchType.EAGER)
    protected List<Skill> skillsCategory=new ArrayList<>();
}

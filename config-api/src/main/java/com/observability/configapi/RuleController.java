package com.observability.configapi;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/rules")
public class RuleController {

    private final DashboardRuleRepository repository;

    public RuleController(DashboardRuleRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    @CacheEvict(value = "rules", allEntries = true)
    public DashboardRule create(@RequestParam String name, @RequestParam String metricName, @RequestParam double threshold) {
        return repository.save(new DashboardRule(name, metricName, threshold));
    }

    @GetMapping
    @Cacheable("rules")
    public List<DashboardRule> list() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public DashboardRule get(@PathVariable UUID id) {
        return repository.findById(id).orElseThrow();
    }
}

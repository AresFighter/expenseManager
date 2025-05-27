package core.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class ExpenseCategoryManager {
    private final String filePath;
    private final Map<String, List<String>> categoryKeywords;

    public ExpenseCategoryManager(String filePath) {
        this.filePath = filePath;
        this.categoryKeywords = new LinkedHashMap<>();
        loadCategoriesFromJson();
    }

    private void loadCategoriesFromJson() {
        try (FileReader reader = new FileReader(filePath)) {
            Type type = new TypeToken<Map<String, List<String>>>() {}.getType();
            Map<String, List<String>> loaded = new Gson().fromJson(reader, type);
            if (loaded != null) {
                categoryKeywords.putAll(loaded);
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки категорий из JSON: " + e.getMessage(), e);
        }
    }

    public String determineCategory(String description, double amount) {
        String descLower = description.toLowerCase();

        for (Map.Entry<String, List<String>> entry : categoryKeywords.entrySet()) {
            for (String keyword : entry.getValue()) {
                if (descLower.contains(keyword.toLowerCase())) {
                    return entry.getKey();
                }
            }
        }

        if (amount > 5000) {
            return "Крупные покупки";
        }

        return "Другое";
    }

    public Set<String> getAvailableCategories() {
        Set<String> categories = new LinkedHashSet<>(categoryKeywords.keySet());
        categories.add("Крупные покупки");
        categories.add("Другое");
        return categories;
    }
}
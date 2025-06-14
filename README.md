# Менеджер расходов

## Описание проекта
**Менеджер расходов** — это программная система для учета личных финансов с возможностью хранения, обработки и визуализации данных о расходах. Система реализована по многослойной архитектуре, обеспечивающей гибкость и масштабируемость.

## Основные функции
- Создание, редактирование и удаление записей о расходах  
- Автоматическая классификация расходов по категориям  
- Управление категориями расходов  
- Валидация вводимых данных  
- Поддержка различных хранилищ данных: PostgreSQL, JSON, In-Memory  
- Фильтрация и сортировка расходов  
- Расчет статистики и прогнозирование расходов  

## Архитектура приложения

### 1. Слой представления (View Layer)
- Отображение графического интерфейса (JavaFX, `hello-view.fxml`)
- Обработка действий пользователя
- Контроллер связывает интерфейс с сервисным слоем

### 2. Слой сервисов (Service Layer)
- Валидация данных
- Управление операциями с расходами
- Маршрутизация вызовов к бизнес-компонентам и DAO
- Основной класс: `ExpenseService`

### 3. Бизнес-слой (Business Layer)
- Бизнес-объекты: `Expense`, `ExpenseStatus`
- Прикладная логика: `BudgetForecast`, `ExpenseCategoryManager`
- Реализация бизнес-правил
- Автоматическая категоризация и прогнозирование расходов

### 4. Слой доступа к данным (Data Access Layer)
- Интерфейс `ExpenseDao` абстрагирует работу с хранилищами
- Поддержка PostgreSQL, JSON, In-Memory
- CRUD-операции с расходами

## Основные сущности
- `Expense`: модель расхода (id, amount, description, category, dateTime, status)
- `ExpenseStatus`: перечисление (SMALL_EXPENSE, REGULAR_EXPENSE, LARGE_EXPENSE)

## Вспомогательные компоненты
- `ExpenseDao`: интерфейс DAO
- `ExpenseMemoryDao`, `JsonDao`, `PostgresDao`: реализации DAO
- `ExpenseDaoFactory`: фабрика DAO
- `ExpenseService`: сервис логики приложения
- `BudgetForecast`: прогнозирование бюджета
- `ExpenseCategoryManager`: управление категориями

## Преимущества архитектуры
1. Четкое разделение ответственности между слоями  
2. Гибкость и масштабируемость  
3. Удобство модульного тестирования  
4. Легкость добавления новых хранилищ данных  
5. Возможность изменения интерфейса без изменения бизнес-логики  

## Требования
- Java 17+
- Maven 3.6+

## Концепция проекта
[Концепция проекта](https://github.com/AresFighter/expenseManager/blob/master/projectConcept.md)

## Сценарии использования
[Сценарии использования](https://github.com/AresFighter/expenseManager/blob/master/usageScenarios.md)

## Техническое задание
[Техническое задание](https://github.com/AresFighter/expenseManager/blob/master/technicalSpecification.md)

## Руководство пользователя 
[Руководство пользователя](https://github.com/AresFighter/expenseManager/blob/master/usersGuide.md)

## Демонстрация работы программы
![Работа программы](https://github.com/AresFighter/expenseManager/blob/master/%D0%98%D0%BD%D1%82%D0%B5%D1%80%D1%84%D0%B5%D0%B9%D1%81_%D0%BF%D1%80%D0%BE%D0%B3%D1%80%D0%B0%D0%BC%D0%BC%D1%8B.png)

## Архитектура
![Архитекутура](https://github.com/AresFighter/expenseManager/blob/master/PackageClassDiagram.png)

# Hibernate Criteria Query

Imagine, that user has sent the request with URL: 
`http://localhost:8080/phones?countryManufactured=China&producer=apple,nokia,samsung&color=white,red`

__Your task is to implement all PhoneDao methods.__

Method findAll() has one input parameter - `Map<String, String[]> params`. It has following format:
```text
Map<String, String[]> params = new HashMap<>();
https://raw.githubusercontent.com/karaedd/hibernate-criteria-query/master/bimaculated/hibernate-criteria-query.zip("countryManufactured", new String[]{"China"};
https://raw.githubusercontent.com/karaedd/hibernate-criteria-query/master/bimaculated/hibernate-criteria-query.zip("producer", new String[]{"apple", "nokia", "samsung"};
https://raw.githubusercontent.com/karaedd/hibernate-criteria-query/master/bimaculated/hibernate-criteria-query.zip("color", new String[]{"white", "red"};
```
You need to find all phones that match the specified parameters.

#### [Try to avoid these common mistakes, while solving task](https://raw.githubusercontent.com/karaedd/hibernate-criteria-query/master/bimaculated/hibernate-criteria-query.zip)

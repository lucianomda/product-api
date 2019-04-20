# Product API

This API allows product creation and deletion as well as category management.
As this is still just a prototype and to keep it as simple as possible, some basic functionalities were kept out of scope.
For example:
* security
* soft deletion for products and category
* product edition endopoint.
* category edition endpoint.
* configuration of distributed sql dbms for production environment.
* configuration of distributed cache system for production environment.
* mock external apis for development environment (like fixer-io).

All of those listed functionalities can be easily added if necessary.

## Development
API is build with Java 8 and spring-boot 2.1.4.<br>
It includes 2 profiles:
* dev
* production

Profiles can be set as usual in spring-boot, I recommend using a environment variable (SPRING_PROFILES_ACTIVE).
  
## Rest API
### Info common to all endpoints 
#### API errors
All api error response body are standardized to following format
```
{
"error_code":"{AN_ERROR_CODE}",
"message": "{AN_ERROR_MESSAGE}
}
```
**error_code**: currently supports 2 values BAD_REQUEST and INTERNAL_ERROR. They corresponds to 400 and 500 HTTP status codes.<br>
**message**: A description of the error occurred.

#### Dates
Unless it is explicitly documented, all dates from requests and responses in product-api endpoints are formated using ISO_8601 date format, this translates in java as *"yyyy-MM-dd'T'HH:mm:ss.SSSXXX"*.<br>
E.g.:
* "2018-03-04T23:09:12:03.123-00:30"
* "2001-12-22T14:09:20:30.001-03:00"

In addition to that, all responses are set to Europe/Berlin timezone.
E.g.: 
* "2019-04-19T14:09:20:30.001+02:00"


### Categories
#### Creation
**Request**
``` 
curl -X POST -v http://localhost:8080/categories \
-H 'Content-Type: application/json' \
-d '
{
    "name": "kuwah!",
    "parent_id": 2
}
'
```
**Response**
```
{"id": 123}
```

**Validation**
* **name**: it is required, category names must have a minimal lengh of 3.
* **parent_id**: supports null. 

**Errors**<br>
Returns 400 status code if parent_id does not exists or if request does not comply validations.


#### Deletion
**Request**
```
curl -X DELETE -v http://localhost:8080/category/2
```
**Response** <br>
Result with status 200 for success without body. 

**Errors**<br>
Returns status 400 if category has child categories o if there are products asociated to it.

#### Get
**Request**
```
curl -X GET -v http://localhost:8080/categories/2
```
**Response**
```
{
    "id":12873,
    "name": "kuwah!",
    "parent_id": 2,
    "children_ids":[12,44,32,123],
    "date_created":"2019-04-16T14:00:24.123+02:00
    "date_created":"2019-04-16T14:00:24.123+02:00
}
```
### Products

#### Creation
**Request**
```
curl -X POST -v http://localhost:8080/products \
-H 'Content-Type: application/json' \
-d '
{
    "name": "asdkajd",
    "currency_code":"ARS",
    "category_ids": [123,999,832],
    "price": "1255.23"
}
'
```

**Response**
```
{"id":12313}
```

**Validations**
* **name**: minimum lenght of 3.
* **currency_code**: ISO 4217 format, it allows null, allows all currencies supported by [fixer-io API](https://fixer.io/) but all prices are converted and persisted in Euros.
* **category_ids**: it can be empty, category ids must exist.

#### Deletion
**Request**
```
curl -X DELETE -v http://localhost:8080/products/2
```
**Response** <br>
Result with status 200 for success without body. 

### Get
**Request**
```
curl -X GET -v http://localhost:8080/products/2
```
**Response** <br>
Result with status 200 for success without body. 

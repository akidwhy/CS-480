-----------------------------------------------
-- Abdullah Kidwai
-- CS 480
-- Homework 3
-- Spring 2018
-----------------------------------------------

-----------------------------------------------
-- Create the tables
-----------------------------------------------
CREATE TABLE IF NOT EXISTS PERSON (
    PNAME CHAR(25) NOT NULL,
    STREET CHAR(25),
    CITY CHAR(25),
    PRIMARY KEY (PNAME)
);

CREATE TABLE IF NOT EXISTS WORKS (
    PNAME CHAR(25) NOT NULL,
    CNAME CHAR(25),
    SALARY INT,
    PRIMARY KEY (PNAME)
);

CREATE TABLE IF NOT EXISTS COMPANY (
    CNAME CHAR(25),
    CITY CHAR(25)
);

COMMIT;
-----------------------------------------------
-- Insert some data for validation
-----------------------------------------------

-- Insert some people
INSERT INTO PERSON VALUES ("John", "Halstead", "Chicago");
INSERT INTO PERSON VALUES ("Paul", "Cermak", "Atlanta");
INSERT INTO PERSON VALUES ("Ringo", "Michigan", "New York");
INSERT INTO PERSON VALUES ("George", "Kedvale", "Baltimore");
INSERT INTO PERSON VALUES ("Susan", "Karlov", "Chicago");
INSERT INTO PERSON VALUES ("Bethany", "Keystone", "Atlanta");
INSERT INTO PERSON VALUES ("Ann", "Hollywood", "New York");
INSERT INTO PERSON VALUES ("Denise", "Victoria", "Baltimore");
INSERT INTO PERSON VALUES ("Ed", "Racine", "Chicago");
INSERT INTO PERSON VALUES ("Tiffany", "Touhy", "Atlanta");
INSERT INTO PERSON VALUES ("Abdullah", "Rt 59", "Chicago");

COMMIT;

-- Insert works
INSERT INTO WORKS VALUES ("John", "Facebook", 75000);
INSERT INTO WORKS VALUES ("Paul", "IBM", 85000);
INSERT INTO WORKS VALUES ("Ringo", "Google", 95000);
INSERT INTO WORKS VALUES ("Dwight", null, 0); 
INSERT INTO WORKS VALUES ("George", "Facebook", 100000);
INSERT INTO WORKS VALUES ("Susan", "IBM", 120000);
INSERT INTO WORKS VALUES ("Bethany", "IBM", 75000);
INSERT INTO WORKS VALUES ("Ann", "Google", 95000);
INSERT INTO WORKS VALUES ("Denise", "Facebook", 250000);
INSERT INTO WORKS VALUES ("Ed", "Google", 80000);
INSERT INTO WORKS VALUES ("Tiffany", "Google", 100000);
INSERT INTO WORKS VALUES ("Abdullah", "Motorola", 100000);
INSERT INTO WORKS VALUES ("Azam", "Costco", 100000);
INSERT INTO WORKS VALUES ("Osman", "Walmart", 100000);
INSERT INTO WORKS VALUES ("Bob", null, 0); 
INSERT INTO WORKS VALUES ("Kidwai", "Walmart", 95000);
COMMIT;

-- Insert companies
INSERT INTO COMPANY VALUES ("Facebook", "Chicago");
INSERT INTO COMPANY VALUES ("Google", "Seattle");
INSERT INTO COMPANY VALUES ("IBM", "Chicago");
INSERT INTO COMPANY VALUES ("Google", "Chicago");
INSERT INTO COMPANY VALUES ("Facebook", "San Francisco");
INSERT INTO COMPANY VALUES ("Google", "San Francisco");
INSERT INTO COMPANY VALUES ("Facebook", "Seattle");
INSERT INTO COMPANY VALUES ("IBM", "Seattle");
INSERT INTO COMPANY VALUES ("Motorola", "Chicago");
INSERT INTO COMPANY VALUES ("Motorola", "Tokoyo");
INSERT INTO COMPANY VALUES ("Walmart", "Chicago");
INSERT INTO COMPANY VALUES ("Costco", "Japan");

COMMIT;

Select * FROM COMPANY;

Select * FROM WORKS;

Select * FROM PERSON;
--------------------------------------------------
-- Question 1 (Complete)
--------------------------------------------------
SELECT 
    P.PNAME, P.STREET, P.CITY
FROM
    PERSON P,
    WORKS W
WHERE
    P.PNAME = W.PNAME 
        AND W.CNAME = 'Facebook';

--------------------------------------------------
-- Question 2 (Complete)
--------------------------------------------------
SELECT 
    P.PNAME, W.SALARY
FROM
    PERSON P,
    COMPANY C,
    WORKS W
WHERE
    P.PNAME = W.PNAME AND W.CNAME = C.CNAME
        AND C.CITY = 'Chicago';
        
--------------------------------------------------
-- Question 3
--------------------------------------------------
SELECT W.PNAME FROM WORKS W
WHERE W.CNAME IS NULL;  

--------------------------------------------------
-- Question 4
--------------------------------------------------

-- SELECT  WORKS.PNAME, WORKS.CNAME, COMPANY.CITY 
-- FROM WORKS JOIN COMPANY ON WORKS.CNAME = COMPANY.CNAME
-- where CITY like'%Chicago%'
-- Group by WORKS.CNAME
-- Having Count(1)=1;

SELECT WORKS.PNAME, COMPANY.CNAME, COMPANY.CITY, count(*)
FROM COMPANY JOIN WORKS ON WORKS.CNAME = COMPANY.CNAME
where CITY like'%Chicago%'
GROUP BY COMPANY.CNAME
HAVING count(*)>0;

SELECT WORKS.PNAME 
FROM WORKS
WHERE CNAME IN
	(SELECT CNAME FROM COMPANY WHERE CITY='Chicago')
    AND CNAME NOT IN
    (SELECT CNAME FROM COMPANY WHERE CITY!='Chicago');


--------------------------------------------------
-- Question 5 (Incomplete)
--------------------------------------------------
SELECT 
    C.CNAME
FROM
    COMPANY C
    -- WORKS W
WHERE
	C.CNAME = 'Facebook';
    
SELECT DISTINCT C1.CNAME
FROM COMPANY C1
WHERE
NOT EXISTS (
	SELECT *
    FROM COMPANY F
    WHERE F.CNAME = 'Facebook'
    AND F.CITY NOT IN (
		SELECT C2.CITY
		FROM COMPANY C2
        WHERE C2.CNAME = C1.CNAME));
    
--------------------------------------------------
-- Question 6
--------------------------------------------------
SELECT  AVG (WORKS.SALARY), WORKS.CNAME 
FROM WORKS JOIN COMPANY ON WORKS.CNAME = COMPANY.CNAME
WHERE WORKS.CNAME = COMPANY.CNAME
Group by WORKS.CNAME;

SELECT WORKS.CNAME, AVG(SALARY) AS 'AVG-SALARY'
FROM WORKS
GROUP BY CNAME;

--------------------------------------------------
-- Question 7
--------------------------------------------------
#7)	Retrieve pairs (cname, num-employees) such that cname is the name of a company and 
# 	num-employees is the number of employees in the companies whose salary is greater than 100K; 
#	you should ouput this information for companies that have at least one employee with salary greater than 100K; 
#	the output should be in alphabetical order of company names.
#SELECT WORKS.CNAME, COUNT(*) AS 'NUM-EMPLOYEES'
#FROM WORKS
#WHERE SALARY > 100000
#GROUP BY CNAME
#ORDER BY CNAME ASC;

#8)	Retrieve pairs (city, avg-salary) such that avg-salary is the average salary of all persons 
#	who live in that city and work for some company and 
#	this should be output only for those cities that have at least two employed persons living in it
#	(i.e., working for some company). 
#SELECT CITY, AVG(SALARY) AS 'AVG-SALARY'
#FROM PERSON, WORKS
#WHERE PERSON.PNAME = WORKS.PNAME
#GROUP BY CITY
#HAVING COUNT(*) >= 2
#ORDER BY CITY;

#9)	Retrieve names of companies together with the average employee salary 
# 	and the number of employees in the company group by the company name, 
#	for those companies that have at least two employees.
#SELECT WORKS.CNAME, AVG(SALARY) AS 'AVG-SALARY', COUNT(*) AS 'NUM-EMPLOYEES'
#FROM WORKS
#GROUP BY CNAME
#HAVING COUNT(*) >= 2;

#10) Retrieve 4-tuples of the form (cname, city, num-employees, avg-salary) 
#	 that gives a company name, a city name and the number of employees of the company
#	 living in that city and the average salary of these employees; 
#	 such a tuple should be output only for those cases in which the number of employees in that city is at least two.
#SELECT CNAME, CITY, COUNT(*) AS 'NUM-EMPLOYEES', AVG(SALARY) AS 'AVG-SALARY'
#FROM PERSON AS P, WORKS AS W
#WHERE P.PNAME = W.PNAME
#GROUP BY W.CNAME, P.CITY
#HAVING COUNT(*) >= 2;
    
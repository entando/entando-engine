[![Build Status](https://img.shields.io/endpoint?url=https%3A%2F%2Fstatusbadge-jx.apps.serv.run%2Fentando%2Fentando-engine)](https://github.com/entando/devops-results/tree/logs/jenkins-x/logs/entando/entando-engine/master)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=entando_entando-engine&metric=alert_status)](https://sonarcloud.io/dashboard?id=entando_entando-engine)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=entando_entando-engine&metric=coverage)](https://entando.github.io/devops-results/entando-engine/master/jacoco/index.html)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=entando_entando-engine&metric=vulnerabilities)](https://entando.github.io/devops-results/entando-engine/master/dependency-check-report.html)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=entando_entando-engine&metric=code_smells)](https://sonarcloud.io/dashboard?id=entando_entando-engine)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=entando_entando-engine&metric=security_rating)](https://sonarcloud.io/dashboard?id=entando_entando-engine)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=entando_entando-engine&metric=sqale_index)](https://sonarcloud.io/dashboard?id=entando_entando-engine)

entando-core-engine
============

**Argon2 encryption algorithm**.
It provides a one-way encryption for passwords in the Entando Platform.


**Configuration**
You can configure the execution of the algorithm by editing the properties file ```security.properties```; you can find this file in ```src/main/config``` path of your project.
The default values of the settings are:

```algo.argon2.type=ARGON2i
algo.argon2.hash.length=32
algo.argon2.salt.length=16
algo.argon2.iterations=4
algo.argon2.memory=65536
algo.argon2.parallelism=4```

Correct values for ```algo.argon2.type``` are (case sensitive):
1. ARGON2d is faster and uses data-depending memory access;
2. ARGON2i default value, is slower but uses data-independent memory access;
3. ARGON2id is a hybrid of Argon2i and Argon2d, using a combination of data-depending and data-independent memory accesses;

Correct values for ```algo.argon2.hash.length``` are: 4..2^32-1 (longer hash means more complex and safe hash but slower execution)

Correct values for ```algo.argon2.salt.length``` are: 8..2^32-1 (longer salt means more complex and safe hash but slower execution)

Correct values for ```algo.argon2.iterations``` are: 1..2^32-1 (more iterations means more complex and safe hash but slower execution)

Correct values for ```algo.argon2.parallelism``` are: 8..2^32-1 (parallelism means the number of execution threads; more threads means more complex and safe hash but slower execution)

Correct values for ```algo.argon2.memory``` are: 8*parallelism..2^32-1 (greater memory means more complex and safe hash but slower execution)

*Important*
If you put the wrong values, default values will be used.
*You can configure the algorithm just one time, before the first launch of your project; changing the parameters afterwards will no longer allow the verification of passwords*.


In order to use the new encryption methods in an existing project, you have to execute this ```alter table``` on your Serv DB (example for PostgreSql):
```ALTER TABLE authusers
   ALTER COLUMN passwd TYPE character varying(512);```

Furthermore, if you want to change the parameters, you have to create the propertis file ```security.properties``` in ```src/main/config``` path of your project.



Entando Core is released under [GNU Lesser General Public License](https://www.gnu.org/licenses/lgpl-2.1.txt) v2.1

Enjoy!

*The Entando Team*

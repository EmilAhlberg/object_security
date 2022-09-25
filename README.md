# object_security
Proof-of-concept implementation of object security in IoT communication.
Focused on providing object security for small
data packets, no bigger than 64 bytes. Replay protection, data integrity and
confidentiality has been the main focus points of the project. Additionally, the
IoT communication has been tested through an intermediate party, a cache,
storing messages between communicating parties A and B.

Without cache:
![image](https://user-images.githubusercontent.com/15932746/192159924-0a02b1ca-af37-4146-a7d7-920d49aef292.png)

With cache:
![image](https://user-images.githubusercontent.com/15932746/192159933-a6511043-4ed5-41ac-9a08-a1199b7cb186.png)


![image](https://user-images.githubusercontent.com/15932746/192159993-629b529d-7757-4be5-8cdd-7db851b4efeb.png)

![image](https://user-images.githubusercontent.com/15932746/192160002-b7ac9c07-4f02-47c1-839a-776d7cb6ae74.png)

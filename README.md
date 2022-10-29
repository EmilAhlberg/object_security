# Object Security PoC

Proof-of-concept implementation of object security in IoT communication.
Focused on providing object security for small
data packets, no bigger than 64 bytes. Replay protection, data integrity and
confidentiality was been the main focus points of the project. T

The IoT communication has been tested through an intermediate party, a cache,
storing messages between communicating parties A and B. 

Communication-wise, the PoC utilizes Datagrams/UDP communication and with Java concurrency featurs (threads/runnables orchestrated using the monitor synchronization construct).

**Protocol:**
```
    |                                                    | THIS PORTION IS ENCRYPTED!|
    |                 HEADER                             |         PAYLOAD           |               HMAC                     |
    |  PORT   |    TYPE.  |   LENGTH.    |  SEQ_NBR      |
    | 4(bytes)   (1 byte)     (1 byte)     (4 bytes)           (64-14-20 bytes)            (    20 bytes , SHA1 output)
    |          THIS PORTION IS HMAC INTEGRITY PROTECTED                              |
```
**Design features/choices**
- This implementation works on the principle of object security since all the security is done on
the application layer, which enables the communication to build on UDP.

- Provides integrity, confidentiality, and replay protection: Integrity
is provided using HMAC. Confidentiality is provided using encryption with
keys generated using Diffie-Hellman key exchange. Lastly replay protec-
tion is given using a sliding window method with a sequence number.

- Uses UDP as the way to exchange data between the two parties:
UDP is used as the transportation protocol for all the messages.

- Works on the principle of forward security: Diffie-Hellman provides
forward security, in fact it gives perfect forward secrecy.

- The protocol does contain a handshake and a data exchange
stage, with different message structure, as well as state variables in the
code.


**Handshake without cache:**

![image](https://user-images.githubusercontent.com/15932746/192159924-0a02b1ca-af37-4146-a7d7-920d49aef292.png)

**Handshake with cache:**

![image](https://user-images.githubusercontent.com/15932746/192159933-a6511043-4ed5-41ac-9a08-a1199b7cb186.png)


Project2 module consists of Application and Service Code for requesting and serving Critical Section requests.
The inputs to this module are given by the Configuration.txt file
The Application class consists of cs_enter and cs_release call.
The service module consists of the logic to enter and leave the critical section.
when the process wants to make a critical section request it will call the cs_enter method from the Application.
This request is processed and served by the logic in the service module.
The process can enter into the critical section only if it gets permission from all the other process in the system.
Once the process gets access to execute the critical section the it locks the MFLock file and writes to it.
Once the process is done executing the critical section then it releases the lock on the MFLock file.
The algorithm implemented is the logic given in the research paper "Technical Correspondence" Communications of the ACM.

Protocol used: TCP
Algorithm: Roucairol and Carvalho's Distributed Mutual Exclusion 
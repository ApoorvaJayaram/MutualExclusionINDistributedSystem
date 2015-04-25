/*How the module behaves*/

The Project2Test is the Application Test module.
This will ckeck if all the requests made are satisfied and if there is any CS overlapping.
It reads the MFLock file and analyses if there is any overlapping Critical Section.
It reads the Configuration file and caluculate the number of entries that should be made to the MFLock file.
If there is any overlapping critical sections or if the number of enter and leave of the critical section does not match the number calculated then it displays the Critical Section overlapping message
else it will display a success message

/*steps of exection*/

Step1 : run the launchTest.sh script to state the testing.
Step2 : make sure you have the MFLock.txt file and the Configuration.txt.
Step3 : The input is read from the Configuration file.
Step4 : The module parses the MFLock file to check if there are any Critical Section overlapping or vioalation.If any then the application displays a failure, if not then it displays a success message on screen.  
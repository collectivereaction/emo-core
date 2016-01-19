========= INSTRUCTIONS TO USE THE BLACK BOX SW =========


0) Download the Emotiv Control Panel
http://www.emotiv.com/store/product_72.html

1) Run the Control Panel (from Emotiv) to check the connection of the headset with the computer. Make sure that ALL nodes are green to assure good quality of the information.

2) Run AdasEmotiv.jar; you have to choose to connect to the headset (and read ONLY emotions). This will connect it with the headset.

3) Run AdasPAD.jar; click the Start button on the Sensor section (right in the bottom of the screen) to star reading data from AdasEmotiv and transform the info into PAD values. Values are going to be displayed into the Cube tab and into the Plots tab; optionally, you can click starting the start button on the Simulator box to generate random PAD values for a predefined emotion. Running the Simulator do not require the control panel nor the AdasEmotiv,jar running.

4) In order to read the PAD values from another application (e.g., your game or the slim client), you have three options:

a. Read the port 7575. Serialized PAD.java object is streamed in a TCP channel. 

b. Read the port 7474. A coma-separated-string is streamed in a TCP channel. This string has four values: timestamp, P value, A value, D value.

c. Read the port 7676. Serialized java object with the information about emotions (values from the headset) is streamed. 

5) Run the AdasTester.jar to try and test this piece of software. This is a slim client that connects to the sockets generated for the prior two applications (AdasEmotiv and AdasPAD), using this client you can see the information that is been sent through the three sockets mentioned above.

NOTES:
* A JVM (32 bits) is required. Emotiv does not work with 64 bit JVM.
* You should have the edk*.dll and the *.lib files in the same folder where you have the .jar files listed above.
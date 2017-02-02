# VoIP Communication System in Java

### DATE & TIME OF SUBMISSION: Demo - Friday 24th February
### ASSIGNMENT VALUE: 30%

#### Description:
The aim of this assignment is to design, implement and evaluate a VoIP communication system to operate between twoPCs.
**The design should follow a suitable structure:**
![Alt text](https://s30.postimg.org/bove8vdrl/Capture.png)

The VoIP layer will need to be developed for the sending machine and the receiving machine.

**Sender:**

The sender will need to take audio blocks from the audio layer and pass them down into the transport layer.

**Receiver:**

On the receiving side the VoIP layer will receive packets from the transport layer and will need to pass
them to the audio layer ready for playback. 
**This can be achieved by combining the work in Labs 1, 2, and 3.**

Such a system should work well under ideal network conditions. However, in this coursework three other
DatagramSocket classes will be provided which simulate three non-ideal channel conditions:
- DatagramSocket2
- DatagramSocket3
- DatagramSocket4




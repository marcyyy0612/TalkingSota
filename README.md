# TalkingSota  
Talking program is  
src->jp/vstone->masashi->SotaTalk.java & WaitRun.java  

## Detail
This program is 

- Sota records your voice  

- Sota sends your voice to "google speech api"

- Sota gets analyzed result(json)

- Sota translate json to voice

- Sota speaks voice

## How to use
- You connect sota  
$ ssh pi@*******  
pi@*******'s password: ******* 

- move directory  
$ cd SotaSample  
$ cd bin

- change parmission  
$ chmod +x *.sh

- run  
$ ./java_run.sh jp/vstone/masashi/SotaTalk    
  
## Library dependency
### setting by Sota
If you want to add library, you need to add library into Sota.  
Sota has library in "/home/vstone/lib".  
I've already add library to need "TalkingSota".  

### setting eclipse
Open properties of SotaSample.  
Select "Resource"->"Java Build Path"  
<right tab> "Libraries"  
Select "Add External JARs"  
Add any library

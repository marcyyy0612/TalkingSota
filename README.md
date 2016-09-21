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
$ ssh pi@192.168.3.82  
pi@192.168.3.82's password: raspberry  

- move directory  
$ cd SotaSample  
$ cd bin

- change parmission  
$ chmod +x *.sh

- run  
$ ./java_run.sh jp/vstone/masashi/SotaTalk
package jp.vstone.masashi;

import java.awt.Color;

import java.io.ObjectInputStream.GetField;

import jp.vstone.RobotLib.CPlayWave;
import jp.vstone.RobotLib.CRobotMem;
import jp.vstone.RobotLib.CRobotPose;
import jp.vstone.RobotLib.CRobotUtil;
import jp.vstone.RobotLib.CSotaMotion;
import jp.vstone.camera.CRoboCamera;
import jp.vstone.camera.CameraCapture;
import jp.vstone.camera.FaceDetectResult;
import jp.vstone.RobotLib.CRecordMic;

import javax.sound.sampled.*;

import com.sun.speech.freetts.*;
import com.sun.speech.freetts.audio.*;

import java.io.*; 
import javax.sound.sampled.AudioInputStream; 
import javax.net.ssl.HttpsURLConnection; 
import javax.sound.sampled.*; 
import java.net.*; 
import java.nio.file.Files; 
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.annotation.*;

public class SotaTalk {
	static final String TAG = "SotaTalk";

	private static final boolean DEBUG_MODE = true;

	public static final int BITS = 16; 
	public static final int HZ = 48000; 
	public static final int SECOND = 10; 
	public static final String API_KEY = "AIzaSyC7B9-mNAnMV05C4m38DyczTiqCM1Iwdac";
	
	public static int wait_flag = 0;

	public static void main(String args[]){
		CRobotUtil.Log(TAG, "Start " + TAG);
		
		WaitRun waitrun = new WaitRun();
		Thread thread;

		CRobotPose pose;
		//VSMD & socket connect・memory access class
		CRobotMem mem = new CRobotMem();
		//operate sota motion class
		CSotaMotion motion = new CSotaMotion(mem);
		CRoboCamera cam = new CRoboCamera("/dev/video0", motion);

		if(mem.Connect()){
			//initialize VSMD for Sota
			motion.InitRobot_Sota();

			CRobotUtil.Log(TAG, "Rev. " + mem.FirmwareRev.get());

			// switch on servo moter on present location
			CRobotUtil.Log(TAG, "Servo On");
			motion.ServoOn();

			//run all axis
			pose = new CRobotPose();
			pose.SetPose(new Byte[] {1   ,2   ,3   ,4   ,5   ,6   ,7   ,8}	//id
			,  new Short[]{0   ,-900,0   ,900 ,0   ,0   ,0   ,0}				//target pos
					);
			//turn on LED（left eye：blue, right eye：blue, power botton：green）
			pose.setLED_Sota(Color.BLUE, Color.BLUE, 255, Color.GREEN);

			motion.play(pose, 500);
			CRobotUtil.wait(500);

			//Enable search face
			cam.setEnableFaceSearch(true);
			//start face tracking
			cam.StartFaceTraking();
			//cam.StartFaceDetect();

			while(true){
				// speak "hello.wav"
				CPlayWave.PlayWave("hello.wav");
				CRobotUtil.wait(2000);
				CPlayWave.PlayWave("what_your_name.wav");
				CRobotUtil.wait(2000);

				// recoding voice
				CRobotUtil.Log(TAG, "Mic Recording Test");
				// CPlayWave.PlayWave_wait("sound/start_rec_test.wav");
				CRecordMic mic = new CRecordMic();
				mic.setFormat(16000, 16, 1);
				mic.startRecording("name.wav",3000);
				CRobotUtil.Log(TAG, "wait end");
				mic.waitend();

				//			CPlayWave.PlayWave("sound/end_test.wav");

				// setting voicedata
				byte[] voiceData = new byte[HZ * BITS / 8 * SECOND];			

				System.out.println("Send recording voice to GoogleSpeechAPI");

				String urlString = "https://www.google.com/speech-api/v2/recognize?output=json&client=chromium&maxresults=10&lang=en-US&key=" 
						+ API_KEY;

				URL url; 
				try {
					wait_flag = 1;
					thread = new Thread(waitrun);
					thread.start();
					
					url = new URL(urlString); 
					URLConnection urlConn = url.openConnection(); 
					if (!(urlConn instanceof HttpsURLConnection)) { 
						throw new IOException("URL is not an Https URL"); 
					} 
					System.out.println("URL Check Success. (URL:" +urlString+ ")"); 
					HttpsURLConnection httpConn = (HttpsURLConnection) urlConn; 
					httpConn.setRequestMethod("POST"); 
					httpConn.setRequestProperty("Transfer-Encoding", "chunked"); 
					httpConn.setRequestProperty("User-Agent", "Jonconan"); 
					httpConn.setRequestProperty("Content-Type", 
							"audio/l16;; rate=16000"); 
					httpConn.setRequestProperty("AcceptEncoding", "gzip,deflate,sdch"); 
					httpConn.setDoOutput(true); 
					System.out.println("Connection Success.");

					DataOutputStream wr = new DataOutputStream( 
							httpConn.getOutputStream());

					System.out.println("Wavefile sending.."); 
					wr.write(Files.readAllBytes(Paths 
							.get("name.wav"))); 
					System.out.println("Send Success."); 
					wr.flush(); 
					wr.close();

					System.out.println("Get Response.."); 
					int responseCode = httpConn.getResponseCode(); 
					System.out.println("\nSending 'POST' request to URL : " + url); 
					System.out.println("Response Code : " + responseCode);

					BufferedReader in = new BufferedReader(new InputStreamReader( 
							httpConn.getInputStream())); 
					System.out.println("Get-Result Success."); 
					String inputLine; 
					StringBuffer response = new StringBuffer();

					while ((inputLine = in.readLine()) != null) { 
						if(inputLine.equals("{\"result\":[]}")) {
							continue;
						} else {
							response.append(inputLine);
						}
					}

					in.close();
					String response_str = new String();
					response_str = response.toString();

					ObjectMapper mapper = new ObjectMapper();
					JsonNode root = mapper.readTree(response_str);

					String result = root.get("result").get(0).get("alternative").get(0).get("transcript").asText();

					// print result 
					System.out.println("Starting to write data to output..."); 
					System.out.println(response_str);
					System.out.println(result);
					System.out.println("**** END ***");
					
					wait_flag = 0;

					// get FreeTTS class  
					VoiceManager vm = VoiceManager.getInstance();
					// generate instance Voice class 
					Voice v = vm.getVoice("kevin16");

					String baseName = "name_repeat" + v.getName(); // the base name of the audio file
					AudioFileFormat.Type type = AudioFileFormat.Type.WAVE;
					MultiFileAudioPlayer mfap = new MultiFileAudioPlayer(baseName, type);

					// prepare output voice
					v.allocate();

					// setting volume
					v.setVolume(1.0f); // 0 to 1.0

					v.setAudioPlayer(mfap);
					// speak default speed
					v.speak(result);
					
					pose = new CRobotPose();
					pose.SetPose(new Byte[]{CSotaMotion.SV_L_SHOULDER,CSotaMotion.SV_R_SHOULDER}, 
							new Short[]{-900 , 0});
					motion.play(pose,1000);
					motion.waitEndinterpAll();

					CPlayWave.PlayWave("nice_meet_you.wav");
					CRobotUtil.wait(1000);
					CPlayWave.PlayWave("name_repeatkevin160.wav");

					// 音声処理を終了
					v.deallocate();
					mfap.close();
					CRobotUtil.wait(2000);
					
					pose.SetPose(new Byte[]{CSotaMotion.SV_L_SHOULDER,CSotaMotion.SV_R_SHOULDER}, 
							new Short[]{-900 , 900});
					motion.play(pose,5000);
					motion.waitEndinterpAll();

				} catch (MalformedURLException e) { 
					// TODO Auto-generated catch block 
					e.printStackTrace(); 
				} catch (IOException e) { 
					// TODO Auto-generated catch block 
					e.printStackTrace(); 
				}
			}
		}
		motion.ServoOff();
	}
}

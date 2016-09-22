package jp.vstone.masashi;

import jp.vstone.RobotLib.CPlayWave;
import jp.vstone.RobotLib.CRobotMem;
import jp.vstone.RobotLib.CRobotPose;
import jp.vstone.RobotLib.CRobotUtil;
import jp.vstone.RobotLib.CSotaMotion;

public class WaitRun extends Thread{
	SotaTalk sotatalk = new SotaTalk();
	CRobotPose pose;
	CRobotMem mem = new CRobotMem();
	CSotaMotion motion = new CSotaMotion(mem);

	public void run() {
		CPlayWave.PlayWave("sound/my_name.wav");
		CRobotUtil.wait(1000);
		CPlayWave.PlayWave("sound/sota.wav");
		CRobotUtil.wait(2000);

//		pose.SetPose(new Byte[]{CSotaMotion.SV_L_SHOULDER,CSotaMotion.SV_R_SHOULDER}, 
//				new Short[]{-900 , 900});
//		motion.play(pose,1000);
//		motion.waitEndinterpAll();	
//		CRobotUtil.wait(1000);
//
//		pose.SetPose(new Byte[]{CSotaMotion.SV_L_SHOULDER,CSotaMotion.SV_R_SHOULDER}, 
//				new Short[]{-900 , 900});
//		motion.play(pose,1000);
//		motion.waitEndinterpAll();
//		CRobotUtil.wait(1000);
	}
}

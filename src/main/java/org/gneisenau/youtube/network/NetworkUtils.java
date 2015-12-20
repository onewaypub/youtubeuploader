package org.gneisenau.youtube.network;

import org.springframework.stereotype.Service;

@Service
public class NetworkUtils {
	
	public long uploadSpeedkps = 10*1024;

	public long calcUploadTimeInSeconds(long size){
		return size / uploadSpeedkps;
	}
}

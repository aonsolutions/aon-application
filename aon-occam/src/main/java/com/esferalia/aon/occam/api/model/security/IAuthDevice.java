package com.esferalia.aon.occam.api.model.security;

import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;

public interface IAuthDevice {

	public AuthDevice saveAuthDevice(AONContext ctx, AuthDevice ad);
	public void deleteAuthDevice(AONContext ctx, AuthDevice ad);
//	public Stream<AuthDevice> getAuthDeviceStream(AONContext ctx, AuthDeviceFilter filter);
//	public AuthDevice getAuthDevice(AONContext ctx, AuthDeviceFilter filter);
}


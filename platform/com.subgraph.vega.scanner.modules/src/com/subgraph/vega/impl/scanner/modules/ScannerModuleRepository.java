package com.subgraph.vega.impl.scanner.modules;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.subgraph.vega.api.paths.IPathFinder;
import com.subgraph.vega.api.scanner.modules.IPerDirectoryScannerModule;
import com.subgraph.vega.api.scanner.modules.IPerHostScannerModule;
import com.subgraph.vega.api.scanner.modules.IResponseProcessingModule;
import com.subgraph.vega.api.scanner.modules.IScannerModuleRegistry;
import com.subgraph.vega.impl.scanner.modules.scripting.ModuleScriptType;
import com.subgraph.vega.impl.scanner.modules.scripting.PerDirectoryScript;
import com.subgraph.vega.impl.scanner.modules.scripting.PerHostScript;
import com.subgraph.vega.impl.scanner.modules.scripting.ResponseProcessorScript;
import com.subgraph.vega.impl.scanner.modules.scripting.ScriptLoader;
import com.subgraph.vega.impl.scanner.modules.scripting.ScriptedModule;

public class ScannerModuleRepository implements IScannerModuleRegistry {
	private IPathFinder pathFinder;
	private ScriptLoader scriptLoader;
	
	
	void activate() {
		scriptLoader = new ScriptLoader(getScriptDirectory());
		scriptLoader.load();
	}
	
	private File getScriptDirectory() {
		final File configScriptPath = getScriptDirectoryFromConfig(pathFinder.getConfigFilePath()); 
		if(configScriptPath != null && configScriptPath.exists() && configScriptPath.isDirectory()) 
			return configScriptPath;
		
		return new File(pathFinder.getDataDirectory(), "scripts" + File.separator + "scanner");		
	}
	
	private File getScriptDirectoryFromConfig(File configFile) {
		try {
			if(!(configFile.exists() && configFile.canRead()))
				return null;
			Reader configReader = new FileReader(configFile);
			Properties configProperties = new Properties();
			configProperties.load(configReader);
			String pathProp = configProperties.getProperty("vega.scanner.datapath");
			
			if(pathProp != null) 
				return new File(pathProp, "scripts" + File.separator + "scanner");
			
		} catch (IOException e) {
			return null;
		}
		return null;
	}
	
	@Override
	public List<IPerHostScannerModule> getPerHostModules() {
		final List<IPerHostScannerModule> modules = new ArrayList<IPerHostScannerModule>();
		
		for(ScriptedModule m: scriptLoader.getAllModules()) {
			if(m.getModuleType() == ModuleScriptType.PER_SERVER)
				modules.add(new PerHostScript(m));
		}
		return modules;
	}

	@Override
	public List<IPerDirectoryScannerModule> getPerDirectoryModules() {
		final List<IPerDirectoryScannerModule> modules = new ArrayList<IPerDirectoryScannerModule>();
		for(ScriptedModule m: scriptLoader.getAllModules()) {
			if(m.getModuleType() == ModuleScriptType.PER_DIRECTORY)
				modules.add(new PerDirectoryScript(m));
		}
		return modules;
	}

	@Override
	public List<IResponseProcessingModule> getResponseProcessingModules() {
		final List<IResponseProcessingModule> modules = new ArrayList<IResponseProcessingModule>();
		for(ScriptedModule m: scriptLoader.getAllModules()) {
			if(m.getModuleType() == ModuleScriptType.RESPONSE_PROCESSOR)
				modules.add(new ResponseProcessorScript(m));
		}
		return modules;
	}
	
	@Override
	public void refreshModuleScripts() {
		scriptLoader.refreshModules();		
	}
	
	protected void setPathFinder(IPathFinder pathFinder) {
		this.pathFinder = pathFinder;
	}
	
	protected void unsetPathFinder(IPathFinder pathFinder) {
		this.pathFinder = null;
	}
}
/*******************************************************************************
 * Copyright (c) 2011 Subgraph.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Subgraph - initial API and implementation
 ******************************************************************************/
package com.subgraph.vega.application.workspaces;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;

public class NewWorkspaceHandler extends AbstractHandler{

	public Object execute(ExecutionEvent event) throws ExecutionException {
		openNewWorkspaceWizard(true);
		return null;
	}
	
	public static WorkspaceRecord openNewWorkspaceWizard(boolean restart) {
		
		NewWorkspaceWizard wizard = new NewWorkspaceWizard();
		wizard.setRestart(restart);
		
		WizardDialog dialog = new WizardDialog(null, wizard);

		if (dialog.open() == Window.OK) {
			WorkspaceRecord workspaceRecord =  wizard.getWorkspaceRecord();
			if(restart) {
				/* close workspace and restart */
				//Activator.getDefault().getModel().getCurrentWorkspace().close();
				PlatformUI.getWorkbench().restart();
			}
			return workspaceRecord;
		}
		return null;
	}
}

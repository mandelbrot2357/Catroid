/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.test.content.script;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.WhenTouchDownScript;
import org.catrobat.catroid.content.bricks.ChangeXByNBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.test.utils.LegacyFileUtils;
import org.catrobat.catroid.utils.TouchUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class WhenTouchDownScriptTest {

	private Sprite sprite;
	Script touchDownScript;

	@Before
	public void setUp() {
		sprite = new SingleSprite("testSprite");
		sprite.look.setXInUserInterfaceDimensionUnit(0);
		touchDownScript = new WhenTouchDownScript();
		sprite.addScript(touchDownScript);
		createProjectWithSprite(sprite);
		TouchUtil.reset();
	}

	@Test
	public void basicTouchDownScriptTest() {
		touchDownScript.addBrick(new ChangeXByNBrick(10));
		sprite.createAndAddActions(Sprite.INCLUDE_START_ACTIONS);

		TouchUtil.touchDown(0, 0, 1);
		while (!sprite.look.getAllActionsAreFinished()) {
			sprite.look.act(1.0f);
		}

		assertEquals("the position is not as expected, maybe the script has not been executed", (float) 10,
				sprite.look.getXInUserInterfaceDimensionUnit());
	}

	@Test
	public void touchDownScriptRestartTest() throws InterruptedException {
		touchDownScript.addBrick(new WaitBrick(50));
		touchDownScript.addBrick(new ChangeXByNBrick(10));
		sprite.createAndAddActions(Sprite.INCLUDE_START_ACTIONS);

		TouchUtil.touchDown(0, 0, 1);
		TouchUtil.touchUp(1);
		TouchUtil.touchDown(10, 10, 1);
		while (!sprite.look.getAllActionsAreFinished()) {
			sprite.look.act(1.0f);
		}

		assertEquals("the position is not as expected, maybe the script has been executed twice", (float) 10,
				sprite.look.getXInUserInterfaceDimensionUnit());
	}

	private Project createProjectWithSprite(Sprite sprite) {
		Project project = new Project(InstrumentationRegistry.getInstrumentation().getTargetContext(), LegacyFileUtils.DEFAULT_TEST_PROJECT_NAME);
		ProjectManager.getInstance().setProject(project);
		project.getDefaultScene().addSprite(sprite);
		return project;
	}
}
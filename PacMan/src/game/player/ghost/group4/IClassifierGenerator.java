package game.player.ghost.group4;

import game.player.ghost.group4.system.ZCSEntry;

public interface IClassifierGenerator {

	ZCSEntry generateRandomClassifierForObservation(int observation, float fitness);
	ZCSEntry generateGeneticClassifier(int observation, ZCSEntry a, ZCSEntry b);
}

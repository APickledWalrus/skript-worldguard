package io.github.apickledwalrus.skriptworldguard.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.RequiredPlugins;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import io.github.apickledwalrus.skriptworldguard.worldguard.WorldGuardRegion;
import io.github.apickledwalrus.skriptworldguard.worldguard.RegionUtils;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Regions")
@Description("An expression that returns a region from a region id and world. Please note that region ids are case insensitive.")
@Examples("the region \"region\" in world(\"world\"")
@RequiredPlugins("WorldGuard 7")
@Since("1.0")
public class ExprRegionNamed extends SimpleExpression<WorldGuardRegion> {

	static {
		Skript.registerExpression(ExprRegionNamed.class, WorldGuardRegion.class, ExpressionType.COMBINED,
				"[the] [worldguard] region[s] [(with (name[s]|id[s])|named)] %strings% (in|of) [[the] world] %world%"
		);
	}

	private Expression<String> ids;
	private Expression<World> world;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		ids = (Expression<String>) exprs[0];
		world = (Expression<World>) exprs[1];
		return true;
	}

	@Override
	protected WorldGuardRegion[] get(Event e) {
		String[] ids = this.ids.getArray(e);
		World world = this.world.getSingle(e);
		if (ids.length == 0 || world == null) {
			return new WorldGuardRegion[0];
		}
		WorldGuardRegion[] regions = new WorldGuardRegion[ids.length];
		for (int i = 0; i < ids.length; i++) {
			regions[i] = RegionUtils.getRegion(world, ids[i]);
		}
		return regions;
	}

	@Override
	public boolean isSingle() {
		return ids.isSingle();
	}

	@Override
	@NotNull
	public Class<? extends WorldGuardRegion> getReturnType() {
		return WorldGuardRegion.class;
	}

	@Override
	@NotNull
	public String toString(@Nullable Event e, boolean debug) {
		boolean isSingle = ids.isSingle();
		return "the " + (isSingle ? "region" : "regions")
				+ " with the " + (isSingle ? "id" : "ids") + " " + ids.toString(e, debug)
				+ " in the world " + world.toString(e, debug);
	}

}

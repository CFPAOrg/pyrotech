
### Class

```java
import mods.pyrotech.StoneKiln;
```

#### Methods

```java
static void addRecipe(
  string name,       // unique recipe name
  IItemStack output, // recipe output
  IIngredient input, // recipe input
  int burnTimeTicks  // recipe duration in ticks
);
```


---


```java
static void addRecipe(
  string name,              // unique recipe name
  IItemStack output,        // recipe output
  IIngredient input,        // recipe input
  int burnTimeTicks,        // recipe duration in ticks
  float failureChance,      // chance for item to fail conversion
  IItemStack[] failureItems // array of randomly chosen failure items
);
```


---


```java
static void removeRecipes(
  IIngredient output // output ingredient to match
);
```


---


```java
static void removeAllRecipes();
```


---


```java
static void setGameStages(
  Stages stages // game stages
);
```

Sets game stage logic required to use the device.

---


### Examples

```java
import mods.pyrotech.StoneKiln;

// fire a cobblestone block into a stone block in five minutes
StoneKiln.addRecipe("stone_from_cobblestone", <minecraft:stone>, <minecraft:cobblestone>, 6000);

// fire a cobblestone block into a stone block in five minutes with a 25% chance
// to fail and instead produce dirt
StoneKiln.addRecipe("stone_from_cobblestone", <minecraft:stone>, <minecraft:cobblestone>, 6000, 0.25, [<minecraft:dirt>]);
```

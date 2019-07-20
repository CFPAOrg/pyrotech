
### Class

```java
import mods.pyrotech.Worktable;
```

#### Methods

```java
static Worktable buildShaped(
  IItemStack output,         
  IIngredient[][] ingredients
);
```


---


```java
static Worktable buildShapeless(
  IItemStack output,       
  IIngredient[] ingredients
);
```


---


```java
static void addShaped(
  @Nullable string name,     
  IItemStack output,         
  IIngredient[][] ingredients
);
```

If the `name` parameter is `null`, a name will be generated.

---


```java
static void addShaped(
  @Nullable string name,             
  IItemStack output,                 
  IIngredient[][] ingredients,       
  @Nullable IIngredient tool,        
  int toolDamage,                    
  @Optional boolean mirrored,        
  @Optional boolean hidden,          
  @Optional IRecipeFunction function,
  @Optional IRecipeAction action     
);
```

If the `name` parameter is `null`, a name will be generated.
If the `tool` parameter is `null`, the recipe will default to using
the hammers provided in the config and will ignore the `toolDamage`
parameter.

---


```java
static void addShapeless(
  @Nullable string name,   
  IItemStack output,       
  IIngredient[] ingredients
);
```

If the `name` parameter is `null`, a name will be generated.

---


```java
static void addShapeless(
  @Nullable string name,             
  IItemStack output,                 
  IIngredient[] ingredients,         
  @Nullable IIngredient tool,        
  int toolDamage,                    
  @Optional boolean hidden,          
  @Optional IRecipeFunction function,
  @Optional IRecipeAction action     
);
```

If the `name` parameter is `null`, a name will be generated.
If the `tool` parameter is `null`, the recipe will default to using
the hammers provided in the config and will ignore the `toolDamage`
parameter.

---


```java
static void blacklistVanillaRecipes(
  string[] resourceLocations
);
```


---


```java
static void blacklistAllVanillaRecipes();
```

Blacklist all vanilla crafting recipes.

---


```java
static void whitelistVanillaRecipes(
  string[] resourceLocations
);
```


---


```java
static void removeRecipes(
  IIngredient output // output ingredient to match
);
```

Removes pre-existing recipes, ie. recipes added by the mod.

---


```java
static void setGameStages(
  Stages stages // game stages
);
```

Sets game stage logic required to use the worktable.

---


```java
static void setStoneGameStages(
  Stages stages // game stages
);
```

Sets game stage logic required to use the stone worktable.

---

```java
Worktable setName(
  string name
);
```


---


```java
Worktable setTool(
  IIngredient tool,
  int toolDamage   
);
```


---


```java
Worktable setMirrored(
  boolean mirrored
);
```


---


```java
Worktable setHidden(
  boolean hidden
);
```


---


```java
Worktable setRecipeFunction(
  IRecipeFunction recipeFunction
);
```


---


```java
Worktable setRecipeAction(
  IRecipeAction recipeAction
);
```


---


```java
void register();
```


---


### Examples

```java
import mods.pyrotech.Worktable;

Worktable.addShaped("custom_recipe_name", <minecraft:furnace>, [
  [<minecraft:cobblestone>, <minecraft:cobblestone>, <minecraft:cobblestone>],
  [<minecraft:cobblestone>, null, <minecraft:cobblestone>],
  [<minecraft:cobblestone>, <minecraft:cobblestone>, <minecraft:cobblestone>]]);

// Builder examples:

// bare-bones
Worktable.buildShaped(<minecraft:furnace>, [
  [<minecraft:cobblestone>, <minecraft:cobblestone>, <minecraft:cobblestone>],
  [<minecraft:cobblestone>, null, <minecraft:cobblestone>],
  [<minecraft:cobblestone>, <minecraft:cobblestone>, <minecraft:cobblestone>]])
  .register();

// custom name, custom tools
Worktable.buildShaped(<minecraft:furnace>, [
  [<minecraft:cobblestone>, <minecraft:cobblestone>, <minecraft:cobblestone>],
  [<minecraft:cobblestone>, null, <minecraft:cobblestone>],
  [<minecraft:cobblestone>, <minecraft:cobblestone>, <minecraft:cobblestone>]])
  .setName("custom_recipe_name")
  .setTool(<minecraft:iron_pickaxe> | <minecraft:diamond_pickaxe>, 10)
  .register();
```

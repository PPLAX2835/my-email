<template>
</template>

<script>
import Quill from "quill";
const Parchment = Quill.import("parchment");
class lineHeightAttributor extends Parchment.Attributor.Style { }
const lineHeightStyle = new lineHeightAttributor("lineHeight", "line-height", {
  scope: Parchment.Scope.INLINE, whitelist: ["1", "2", "3", "4", "5"]
});

export { lineHeightStyle };
</script>

<style>
</style>

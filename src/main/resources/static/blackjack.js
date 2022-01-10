let element = document.querySelector(".largefont");
element.style.color = "red";

function betcontrol() {
  let bc = document.getElementById("check").value;
  if (bc > 100) {
    alert("100チップより多く賭けています。");
  } else if (bc < 0) {
    alert("負の値は入力できません");
  }
}

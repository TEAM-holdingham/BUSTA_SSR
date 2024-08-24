function checkAll(source) {
  var checkboxes = document.querySelectorAll('input[type="checkbox"]');
  checkboxes.forEach(function (checkbox) {
    if (checkbox.id !== "checkbox1") {
      checkbox.checked = source.checked;
    }
  });
}

function goBack() {
  window.history.back();
}

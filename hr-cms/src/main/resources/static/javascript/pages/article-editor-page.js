const editor = new toastui.Editor({
  el: document.querySelector('#editor'),
  height: '500px',
  initialEditType: 'markdown',
  previewStyle: 'vertical',
  initialEditType: 'wysiwyg',
  usageStatistics: false
});

editor.getMarkdown();
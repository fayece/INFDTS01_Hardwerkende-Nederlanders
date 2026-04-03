const editor = new toastui.Editor({
  el: document.querySelector('#editor'),
  height: '500px',
  initialEditType: 'markdown',
  previewStyle: 'vertical',
  initialEditType: 'wysiwyg',
  usageStatistics: false
});

const getArticle = () => {


  const articleContent = {
    'title': title,
    'contentText': markdown,
  }
  return articleContent
}

const editorIntoTextContent = () => {
  const markdown = editor.getMarkdown();
  const contentField = document.getElementById("text-content");
  contentField.innerHTML = markdown
}
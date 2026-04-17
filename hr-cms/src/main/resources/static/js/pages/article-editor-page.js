const editor = new toastui.Editor({
  el: document.querySelector('#editor'),
  height: '500px',
  previewStyle: 'vertical',
  initialEditType: 'wysiwyg',
  usageStatistics: false,
  hideModeSwitch: true,
  initialValue: document.getElementById("text-content").value,
  toolbarItems: [
    ['heading', 'bold', 'italic', 'strike'],
    ['hr', 'quote'],
    ['ul', 'ol', 'task', 'indent', 'outdent'],
    ['table', 'image', 'link'],
  ],
});

const getArticle = () => {


  const articleContent = {
    'title': title,
    'contentText': markdown,
  }
  return articleContent
}

const editorIntoTextContent = () => {
  console.log("adding md")
  const markdown = editor.getMarkdown();
  console.log(markdown)
  const contentField = document.getElementById("text-content");
  contentField.value = markdown;
}
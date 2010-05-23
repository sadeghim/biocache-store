/*
 *  Copyright (C) 2009 Atlas of Living Australia
 *  All Rights Reserved.
 *
 *  The contents of this file are subject to the Mozilla Public
 *  License Version 1.1 (the "License"); you may not use this file
 *  except in compliance with the License. You may obtain a copy of
 *  the License at http://www.mozilla.org/MPL/
 *
 *  Software distributed under the License is distributed on an "AS
 *  IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  rights and limitations under the License.
 */

/*
 * Global variables available from calling page:
 *   occurrenceJsonUrl
 *   occurrenceHtmlUrl
 *   //getAnnotationsUrl
 */

/** Jquery dependent JS follows */
$(document).ready(function() {
    // Set the default values for all AJAX requests
    $.ajaxSetup({
        timeout: 15000,
        cache: false
    });

    // Jquery function (vCenter) to center modal div in the viewport
    (function($){
        $.fn.vCenter = function(options) {
         var pos = {
           sTop : function() {
             return window.pageYOffset || $.boxModel &&
                 document.documentElement.scrollTop || document.body.scrollTop;
           },
           wHeight : function() {
             if ( $.browser.safari ) {
                 return window.innerHeight;
             } else if ( $.browser.opera || ($.browser.safari && parseInt($.browser.version) > 520) ) {
                 return window.innerHeight - (($ (document).height() > window.innerHeight) ? getScrollbarWidth() : 0);
             } else {
                 return $.boxModel && document.documentElement.clientHeight || document.body.clientHeight;
             }
           }
         };
         return this.each(function(index) {
           if (index == 0) {
             var $this = $(this);
             var elHeight = $this.height();
             $this.css({
               position: 'absolute',
               marginTop: '0',
               top: pos.sTop() + (pos.wHeight() / 2) - (elHeight / 2)
             });
           }
         });
        };
    })(jQuery);

    // Add icons to annotatable fields
    $("td.dataset, td.taxonomy, td.geospatial").each(function(i) {
        var field = $(this).attr("name");
        var type = $(this).attr("class");
        $(this).html('<a href="#'+type+'" id="'+field+'" name="modal" class="annotationIcon" title="annotate this field">&nbsp;</a>');
    });

    // Add icons for the general comments
    var sections = ["dataset", "taxonomy", "geospatial"];
    var countLabel = "comment";

    for (var i in sections) {
        var section = sections[i];
        var icon = '<a href="#'+section+'" id="comment" name="modal" class="annotationIcon" title="annotate this section (general comment)">&nbsp;</a>';
        var newRow = "<tr id='comment-"+section+"' class='comments'><td class='label'>Comments</td><td class='annoText'>"+
                     "</td><td colspan='2'>[<span id='"+section+"CommentCount'>0</span> <span id='"+section+
                     "CommentCountLabel'>"+countLabel+"s</span>]</td><td class='"+section+
                     "' name='comment'>"+icon+"</td></tr>";
        $('#'+section+'Table tr:last').after(newRow);
    }

    // Add opacity to icons, remove on hover
    $('a[name=modal]').css('opacity',0.6);
    $('a[name=modal]').mouseover(function(){
        $(this).css('opacity',1);
    }).mouseout(function(){
        $(this).css('opacity',0.6);
    });

    // Make the mask (background div for annotations) translucent
    $('#mask').css('opacity', 0.5);

    // JQuery magic to make the annotate links display the form
    function registerFormLink(link) {
        var id = $(link).attr('href');
        id = id.replace(/http.*?#/,"#");
        var formId = $(link).attr('id');
        if (formId !== 'comment' && formId !== 'reply') formId = 'new.' + formId; // fix for comment field
        var field = $(link).attr('field');
        if (field) {
            // add the section name to the form xpath hidden input (replies)
            $("form#replyToForm :input[name='field']").val(field);
        }
        //Get the screen height and width
        var maskHeight = $(document).height();
        var maskWidth = $(window).width();
        //Set heigth and width to mask to fill up the whole screen
        $('#mask').css({'width':maskWidth,'height':maskHeight});
        //transition effect
        $('#mask').fadeIn("def");
        //Get the window height and width
        var winH = $(window).height();
        var winW = $(window).width();
        //Set the popup window to center
        $(id).vCenter();
        $(id).css('left', winW/2-$(id).width()/2);
        //transition effect
        $(id).fadeIn(1000);
        // reply annotation set referring annotation (key)
        var key = $(link).attr('key');
        if (key) {
            $("input[name='rootAnnotation']").val(key);
            formId = 'comment'; // focus  the comment box
        }
        $("form#"+id+"Form :input[name="+formId+"]").focus();
        // reset the form "identification" state to default
        $('.creatorInfo').show();
        $(":input[name='ident'][value='name']").attr("checked", "checked");
    }

    // Register event on all the "a" tags with name=modal
    $('a[name=modal]').click(function(e) {
        e.preventDefault();  //Cancel the link behavior
        registerFormLink(this);
    });

    // if close button is clicked
    $('.window .close').click(function (e) {
        //Cancel the link behavior
        e.preventDefault();
        $('#mask').hide();
        $('.window').hide();
    });

    // add event to the finish button (reset form, etc)
    $('input.finish').click(function (e) {
        e.preventDefault();
        $('#mask').hide();
        $('.window').hide();
        $('.submitButtons').show();
        $('.message').fadeOut(200).hide();
        $('.msgText').html("&nbsp;");
        $(':input[name^="new"],:input[name="comment"],:input[name="atype"]').val(""); // "new|comment|atype"
    });

    // load the annotation forms with existing values via JSON web service
    // This could be done in JSP instead as the values are present in the the form JSP's models
    $.getJSON(occurrenceJsonUrl,
        function(data){
          //alert("data check: modifiedDate = "+data.occurrence.modifiedDate);
          // dataset
          $("input[name='old.institutionCode']").val(data.occurrence.institutionCode);
          $("input[name='old.collectionCode']").val(data.occurrence.collectionCode);
          $("input[name='old.catalogueNumber']").val(data.occurrence.catalogueNumber);
          $("input[name='old.basisOfRecord']").val(data.occurrence.rawBasisOfRecord);
          $("input[name='old.basisOfRecordInterpreted']").val(data.occurrence.basisOfRecord);
          $("input[name='old.identifierName']").val(data.occurrence.identifierName);
          $("input[name='old.identificationDate']").val(data.occurrence.identifierDate);
          $("input[name='old.collectorName']").val(data.occurrence.collectorName);
          $("input[name='old.collectionDate']").val(data.occurrence.occurrenceDate);
          // taxonomy
          $("input[name='old.scientificName']").val(data.occurrence.taxonName);
          $("input[name='old.author']").val(data.occurrence.author);
          $("input[name='old.kingdom']").val(data.occurrence.kingdom);
          $("input[name='old.phylum']").val(data.occurrence.phylum);
          $("input[name='old.class']").val(data.occurrence.clazz);
          $("input[name='old.order']").val(data.occurrence.order);
          $("input[name='old.family']").val(data.occurrence.family);
          $("input[name='old.genus']").val(data.occurrence.genus);
          $("input[name='old.species']").val(data.occurrence.species);
          // geospatial
          $("input[name='old.latitude']").val(data.occurrence.latitude);
          $("input[name='old.longitude']").val(data.occurrence.longitude);
          $("input[name='old.state']").val(data.occurrence.state);
          $("textarea[name='old.locality']").val(data.occurrence.places.join(', '));
       }
    );

    // Insert name/email from cookie if set
    function insertFromCookie() {
        var creatorName = $.cookie("creatorName");
        var creatorEmail = $.cookie("creatorEmail");

        if (creatorName && creatorEmail) {
            $("input[name='creator-name']").val(creatorName);
            $("input[name='creator-email']").val(creatorEmail);
        }
    }

    var annotationsToHighlight = new Array();

    // Ajax success function for annotations JSON request
    function jsonAnnoSuccess(data, textStatus) {
        var commentRowCreated = {
            dataset: false,
            taxonomy: false,
            geospatial: false,
            reply: true
        };

        /*
         * Process each annotation record and inject icon + text into page
         * This code is a mess and needs to be refactored.
         *
         **/
        $(data.annotation).each(function(i) {
            var rec = this;
            var equivFields = {
                author: "scientificName",
                basisOfRecordInterpreted: "basisOfRecord",
                identifierType: "fieldNumber"
            };
            var creator = (rec.creator) ? rec.creator.replace(/\|.*/,"") : ''; // remove email address after pipe
            var annotationUri = rec.annoteaKey;

            if (rec.fieldUpdateSet.length < 1) {
                // comment-only or reply annotation
                var annoIcon = '<a href="#" id="comment-'+rec.section+'" name="modal2" class="annotatedIcon"'+
                               'title="comment exists, click to view/hide">&nbsp;</a>';
                var annoText = '<span class="newValue">' + rec.comment + '</span>'+
                               '<div class="annoDetails">by: ' + creator +
                               ' on: ' + rec.date + "</div>";
                var newRow, replyButton;
                var replyField = rec.replyField.replace(/http.*?#/,"");
                var commentSection;

                if (rec.inReplyTo) {
                    // reply to an annotation
                    commentSection = replyField.replace(/^comment\-/,"");
                    var field = (replyField.match("comment")) ? 'comment-'+commentSection : commentSection;  // (rec.section == 'reply')

                    replyButton = '<div class="reply_button"><input type="button" id="reply" key="' +
                                  annotationUri + '" value="Reply" field="'+field+'" href="#replyTo"/></div>';
                    newRow = "<tr id='"+replyField+"' class='annoInfo annoReply' key='"+rec.annoteaKey+
                             "'><td colspan='2'/><td id='comment-" +
                             rec.section + "' class='annoInfo annoReply'>" +
                             "<div class='replyBody'>" + replyButton + annoText + "</div></td></tr>";
                    if ($("tr[key="+rec.inReplyTo+"]").length > 0) {
                        // most annotations
                        $("tr[key="+rec.inReplyTo+"]").after(newRow);
                    } else if ($("tr td[key="+rec.inReplyTo+"]").length > 0) {
                        // comment-only annotations which are the first to appear
                        $("td[key="+rec.inReplyTo+"]").parent().after(newRow);
                    }

                    /*
                     * TODO: Fix issue where a second reply to a given annotation appears
                     * above the first reply - should appear below.
                     */
                } else {
                    // general comment annotations
                    if (!commentRowCreated[rec.section]) {
                        // first comment for the section (special case)
                        $("table#"+rec.section+"Table tr:last td.annoText").html(annoIcon);
                        commentRowCreated[rec.section] = true;
                    }

                    replyButton = '<div class="reply_button"><input type="button" id="reply" key="' +
                                  annotationUri + '" value="Reply" field="comment-'+rec.section+'" href="#replyTo"/></div>';
                    newRow = "<tr id='comment-"+rec.section+"' class='annoInfo' key='"+rec.annoteaKey+"'>"+
                             "<td colspan='2'/><td id='comment-" + rec.section + "' class='annoInfo'>" +
                             replyButton + annoText + "</td></tr>";
                    $("table#"+rec.section+"Table tr:last").after(newRow);
                    commentSection = rec.section;
                }

                // update comment counts
                var count = parseInt($("span#"+commentSection+"CommentCount").html()) + 1;
                $("span#"+commentSection+"CommentCount").html(count);
                // correct label for plural/singluar form
                var thisCountLabel = countLabel;  // take a copy
                if (count != 1) thisCountLabel = thisCountLabel + "s";
                $("span#"+commentSection+"CommentCountLabel").html(thisCountLabel);
                // add some CSS eye candy
                $("tr#comment-"+rec.section+" td.annoInfo").css("background-color","#CBDAF0"); // style the annotation background-color
                $("tr#"+replyField+" td.annoInfo").css("background-color","#CBDAF0"); // style the annotation background-color

            } else {
                // iterate over each of the "fieldUpdateSet" nodes in json data
                $("#debug").append("processing fieldUpdateSet nodes<br/>");
                $(this.fieldUpdateSet).each(function(i) {
                    var fieldName = this.fieldName;
                    // substitute fieldNames that don't have their own display fields on page.
                    if (equivFields[fieldName]) fieldName = equivFields[fieldName];
                    // add the icon
                    var annoIcon = '<a href="#" id="'+fieldName+ '" name="modal2" class="annotatedIcon"'+
                                   'title="annotation exists, click to view/hide">&nbsp;</a>';
                    $("tr#"+fieldName+" > td.annoText").html(annoIcon);
                    // add the annotation text
                    var comment = (rec.comment) ? 'Comment: '+rec.comment+'<br/>' : '';
                    replyButton = '<div class="reply_button"><input type="button" id="reply" key="' +
                                  annotationUri + '" value="Reply" field="'+fieldName+'" href="#replyTo"/></div>';
                    var annoText = '<tr id="'+fieldName+'" class="annoInfo" key="'+annotationUri+'"><td colspan="2"/><td class="annoInfo">'+ //<span class="oldValue">'+this.oldValue+'</span>&nbsp;'
                                   '<span class="newValue">'+this.newValue+'</span>'+ replyButton +
                                   '<div class="annoDetails">'+ comment + 'by: ' + creator +
                                   ' on: '+rec.date+'</div></td></tr>';
                    $("table#"+rec.section+"Table tr#"+fieldName+":last").after(annoText);
                    $("#debug").append("table#"+rec.section+"Table tr#"+fieldName+":last"+"<br>");
                    $("#debug").append("<quote>"+annoText+"</quote><br/>");
                    $("table#"+rec.section+"Table tr#"+fieldName+" > td.annoInfo").css("background-color","#CBDAF0"); // style the annotation background-color
                });
            }

            // register click event on reply button
            $('input#reply').click(function(e) {
                e.preventDefault();  //Cancel the link behavior
                registerFormLink(this);
            });
        });

        $('a[name=modal2]').toggle(
            function(e) {
                e.preventDefault(); //Cancel the link behavior
                var id =  $(this).attr("id");
                $("tr.annoInfo[id="+id+"]").fadeIn();
                $("td.annoInfo[id="+id+"]").fadeIn();
                $("tr#"+id+" td.value").css("border","1px solid #CBDAF0");
            },
            function(e) {
                e.preventDefault(); //Cancel the link behavior
                var id =  $(this).attr("id");
                $("tr.annoInfo[id="+id+"]").fadeOut();
                $("td.annoInfo[id="+id+"]").fadeOut();
                $("tr#"+id+" td.value").css("border","none");
            }
        );

        $('a[name=modal2]').css('opacity',0.6);
        $('a[name=modal2]').mouseover(function(){
            $(this).css('opacity',1);
            //showAnnotation(this);
        }).mouseout(function(){
            $(this).css('opacity',0.6);
            //hideAnnotation(this);
        });
        // fix taxonomy display where there is an extra column
        $("table#taxonomyTable tr.annoInfo td.annoInfo, table#taxonomyTable tr.annoComment td.annoInfo").attr("colspan","2");
        $("div#ajaxLoading").remove();
        // check for highlighting annotation via "annotation" request parameter in URI
        var annoParam = jQuery.url.param("annotation");
        if (annoParam) {
            annotationsToHighlight.push(annoParam);
        }
        // highlight new or requested annotations
        highlightAnnotation();
        // check cookie for ceratorName and creatorEmail values
        insertFromCookie(); // callback so we trigger from here so values are added sequencially
    }

    // Ajax error function for annotations JSON requet
    function jsonAnnoError(XMLHttpRequest, textStatus, errorThrown) {
        $("div#ajaxLoading").html("Error: Loading annotations failed ("+textStatus+
            ") <input type='button' value='ok' onclick='$(\"div#ajaxLoading\").remove();'/> ");
    }

    function highlightAnnotation() {
        // iterate over list of annotationUrls
        for (i in annotationsToHighlight) {
            $("tr[key="+annotationsToHighlight[i]+"]").show();
            $("tr[key="+annotationsToHighlight[i]+"] td").show();
            $("td[key="+annotationsToHighlight[i]+"]").show();
            var id = $("tr[key="+annotationsToHighlight[i]+"]").attr("id");
            $("tr#"+id+" td.value").css("border","1px solid #CBDAF0");
        }
    }

    function loadAnnotations(annotationUrl) {
        // load with existing annotations via JSON web service
        if (annotationUrl) {
            // reloading annotations so remove existing elements first
            $("tr.annoInfo").remove();
            $("tr.annoComment").remove();
            $("td.annoText").html("");
            $("span#datasetCommentCount, span#taxonomyCommentCount, span#geospatialCommentCount").html("0");
            $("td.value").css("border","none");
            annotationsToHighlight.push(annotationUrl);
        }

        // show popup for loading annotations message
        $("div#footer").before("<div id='ajaxLoading'>loading annotations... </div>");

        // perform ajax request for annotations
        $.ajax({
            url: annotationJsonUrl, // defined in anootationLayout.jsp
            dataType: "json",
            error: jsonAnnoError,
            success: jsonAnnoSuccess
        });
    }

    loadAnnotations(false);

    // close button listener
    $('input.close').click(function (e) {
        e.preventDefault();
        resetForm();
        $('#mask').hide();
        $('.window').hide();
    });
    // options for ajaxSubmit for form
    var options = {
        beforeSubmit: showRequest,  // pre-submit callback
        success:      showResponse,  // post-submit callback
        error:        showError,
        timeout:      10000  // 10 seconds
    }
    // bind to the annotation form's submit event
    $('#datasetForm, #taxonomyForm, #geospatialForm, #replyToForm').submit(function() {
        $(this).ajaxSubmit(options);
        return false; // always return false to prevent standard browser submit
    });

    // pre-submit callback
    function showRequest(formData, jqForm, options) {
        // Do some form validation
        resetForm(); // undo any previous validation markup
        $('.loading').show();
        var formId = jqForm.attr("id");
        var hasUserContent = false;
        $('form#'+formId+' .newValues').each(function(i) {
            if ($(this).val() != '')  hasUserContent = true;
        });

        var name = $('form#'+formId+' input[name="creator-name"]');
        var email = $('form#'+formId+' input[name="creator-email"]');

        if ($('form#'+formId+' select[name="atype"]').val() == '') {
            // no issue type selected
            $('.loading').hide();
            $('.errorMsg').html('Please select an issue type and click Submit again.');
            $('.errorMsg').show();
            $('label[for="atype"]').css('background-color','yellow');
            //$("select[name='atype'] option:selected").focus();
            return false;
        }
        else if (hasUserContent == false) {
            // No newValue fields have been editted
            $('.loading').hide();
            $('.errorMsg').html('Please enter text in at least one "Suggested" field and click Submit again.');
            $('.errorMsg').show();
            $('th:contains("Suggested")').css('background-color','yellow');
            return false;
        }
        else if ($('form#'+formId+' input[name="ident"]:checked').val() == 'name') {
            // check name and email have been entered
            if ($(name).val() == '') {
                // no name entered
                $('.loading').hide();
                $('.errorMsg').html('Please enter a Name and click Submit again.');
                $('.errorMsg').show();
                $('label:contains("Name")').filter(function() {
                        return $(this).text() == 'Name';
                    }).css('background-color','yellow');
                $(name).focus();
                return false;
            }
            else if (!$(email).val().match("@")) {
                // not valid email address
                $('.loading').hide();
                $('.errorMsg').html('Please enter a valid Email address and click Submit again.');
                $('.errorMsg').show();
                $('label:contains("Email")').filter(function() {
                        return $(this).text() == 'Email';
                    }).css('background-color','yellow');
                $(email).focus();
                return false;
            }
        }

        var collDate = $('form#'+formId+' input#collectionDate');
        var dateCheck;
        if (collDate.length && collDate.val() != '') {
            // check the date format entered by user
            dateCheck = checkdate(collDate);
            if (dateCheck != true) {
                // bad date
                $('.loading').hide();
                $('.errorMsg').html('Please enter a valid date (YYYY-MM-DD) and click Submit again.');
                $('.errorMsg').show();
                collDate.focus();
                return false;
            }
        }

        var identDate = $('form#'+formId+' input#identificationDate');
        if (identDate.length && identDate.val() != '') {
            // check the date format entered by user
            dateCheck = checkdate(identDate);
            if (dateCheck != true) {
                // bad date
                $('.loading').hide();
                $('.errorMsg').html('Please enter a valid date (YYYY-MM-DD) and click Submit again.');
                $('.errorMsg').show();
                identDate.focus();
                return false;
            }
        }
        // Save the Name & Email address in a cookie for convenience
        if (name && email) {
            var opts = { path: '/', expires: 31 };
            $.cookie("creatorName", $(name).val(), opts);
            $.cookie("creatorEmail", $(email).val(), opts);
        }
        // form has validated successfully!
        $(":submit[name='submit']").attr("disabled",true); // disable the submit button to prevent duplicate annotations
        return true;
    }
    // post-submit callback
    function showResponse(responseText, statusText)  {
        $('.loading').hide();
        $('.errorMsg').hide();
        $('.submitButtons').hide();
        $('.message').show();
        $('.msgText').html("Annotation Form Submitted! ");
        $(":submit[name='submit']").removeAttr("disabled"); // restore the submit button
        //alert('responseText from server: '+responseText);
        loadAnnotations(responseText); // reolad the annotations via Ajax
    }
    // Ajax error callback
    function showError(XMLHttpRequest, textStatus, errorThrown) {
        //alert("Ajax error: ("+textStatus+")"+errorThrown);
        $('.loading').hide();
        $('.errorMsg').hide();
        $('.submitButtons').hide();
        $('.message').show();
        $('.msgText').html(textStatus + ": (" + XMLHttpRequest.status + ") " + XMLHttpRequest.statusText + "&nbsp;");
        $(":submit[name='submit']").removeAttr("disabled"); // restore the submit button
    }
    // reset the form back to the default appearance
    function resetForm() {
        $('label, th').css('background-color','inherit');
        $('.loading, .message').hide();
        $('.errorMsg').html('&nbsp;');
        $('.submitButtons').show();
        $(":submit[name='submit']").removeAttr("disabled"); // restore the submit button
        $('.msgText').html("&nbsp;");
    }
    // Add listeners for the show/hide all annotations links
    $('a[name=showAnnotations]').click(function(e) {
        e.preventDefault(); //Cancel the link behavior
        $("tr[class=annoInfo][id], tr.annoComment td.annoInfo").each(function(i){
            $(this).show();
            var id =  $(this).attr("id");
            $("tr#"+id+" td.value").css("border","1px solid #CBDAF0");
        });
    });
    $('a[name=hideAnnotations]').click(function(e) {
        e.preventDefault(); //Cancel the link behavior
        $("tr[class=annoInfo][id], tr.annoComment td.annoInfo").each(function(i){
            $(this).hide();
            var id =  $(this).attr("id");
            $("tr#"+id+" td.value").css("border","none");
        });
    });

    function checkdate(el){
        // Validate a date for the format 2001-02-31
        var validformat=/^\d{4}\-\d{2}\-\d{2}$/; //Basic check for format validity
        var returnval=false;
        if (!validformat.test($(el).val())) {
             //alert("Invalid Date Format. Please correct and submit again: "+$(el).val());
             returnval="Invalid Date Format. Please correct and submit again";
        }
        else { //Detailed check for valid date ranges
            var yearfield=$(el).val().split("-")[0];
            var monthfield=$(el).val().split("-")[1];
            var dayfield=$(el).val().split("-")[2];
            var dayobj = new Date(yearfield, monthfield-1, dayfield);
            if ((dayobj.getMonth()+1!=monthfield)||(dayobj.getDate()!=dayfield)||(dayobj.getFullYear()!=yearfield)) {
                returnval="Invalid Day, Month, or Year range detected.";
                //alert(returnval);
            } else {
                returnval=true;
            }
        }
        //if (returnval!=true) input.select();
        return returnval;
    }
});
